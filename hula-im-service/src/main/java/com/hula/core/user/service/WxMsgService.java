package com.hula.core.user.service;

import cn.hutool.core.util.RandomUtil;
import com.hula.common.constant.MqConstant;
import com.hula.common.constant.RedisKey;
import com.hula.common.domain.dto.LoginMessageDTO;
import com.hula.common.domain.dto.ScanSuccessMessageDTO;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.service.adapter.TextBuilder;
import com.hula.core.user.service.adapter.UserAdapter;
import com.hula.service.MQProducer;
import com.hula.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 处理与微信api的交互逻辑
 *
 * @author nyh
 */
@Service
@Slf4j
public class WxMsgService {
    /**
     * 用户的openId和前端登录场景code的映射关系
     */
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    /**
     * 微信事件前缀
     */
    private static final String PREFIX = "qrscene_";

    /**
     * 回调地址
     */
    private static final String LOCAL_CALLBACK_SUFFIX = "/wx/portal/public/callBack";

    @Value("${wx.mp.callback}")
    private String callback;

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Resource
    private LoginService loginService;

    @Resource
    private MQProducer mqProducer;

    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage) {
        String openid = wxMpXmlMessage.getFromUser();
        Integer loginCode = Integer.parseInt(getEventKey(wxMpXmlMessage));
        User user = userDao.getByOpenId(openid);
        // 如果已经注册,直接登录成功
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getAvatar())) {
            mqProducer.sendMsg(MqConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), loginCode));
            return null;
        }

        // user为空先注册,手动生成,以保存uid
        if (Objects.isNull(user)) {
            loginService.wxRegister(User.builder().openId(openid).build());
        }
        // 在redis中保存openid和场景code的关系，后续才能通知到前端,旧版数据没有清除,这里设置了过期时间
        RedisUtils.set(RedisKey.getKey(RedisKey.OPEN_ID_FORMAT, openid), loginCode, 60, TimeUnit.MINUTES);
        // 授权流程,给用户发送授权消息，并且异步通知前端扫码成功,等待授权
        mqProducer.sendMsg(MqConstant.SCAN_MSG_TOPIC, new ScanSuccessMessageDTO(loginCode));
        String skipUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(),
                URLEncoder.encode(callback + LOCAL_CALLBACK_SUFFIX, StandardCharsets.UTF_8));
        // TODO 貌似没啥用？
        // WxMpXmlOutMessage.TEXT().build();
        return new TextBuilder().build("请点击链接授权：<a href=\"" + skipUrl + "\">登录</a>", wxMpXmlMessage, wxMpService);
    }

    private String getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        // 扫码关注的渠道事件有前缀，需要去除
        return wxMpXmlMessage.getEventKey().replace(PREFIX, "");
    }

    /**
     * 用户授权
     *
     * @param userInfo 微信用户信息
     */
    public void authorize(WxOAuth2UserInfo userInfo) {
        User user = userDao.getByOpenId(userInfo.getOpenid());
        // 更新用户信息
        if (StringUtils.isEmpty(user.getName())) {
            fillUserInfo(user.getId(), userInfo);
        }
        // 找到对应的code
        Integer code = RedisUtils.get(RedisKey.getKey(RedisKey.OPEN_ID_FORMAT, userInfo.getOpenid()), Integer.class);
        // 发送登录成功事件
        mqProducer.sendMsg(MqConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), code));
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User fillUser = UserAdapter.buildAuthorizeUser(uid, userInfo);
        // TODO 循环防止账号重复，存在bug
        for (int i = 0; i < 5; i++) {
            try {
                userDao.updateById(fillUser);
                return;
            } catch (DuplicateKeyException e) {
                log.info("fill userInfo duplicate uid:{},info:{}", uid, userInfo);
            } catch (Exception e) {
                log.error("fill userInfo fail uid:{},info:{}", uid, userInfo);
            }
            fillUser.setAccount(userInfo.getNickname() + RandomUtil.randomInt(100000));
            fillUser.setPassword(userInfo.getNickname() + RandomUtil.randomInt(100000));
        }
    }
}
