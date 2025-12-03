package com.luohuo.flex.im.core.user.service;

import cn.hutool.core.lang.UUID;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.service.MQProducer;
import com.luohuo.flex.common.cache.common.WxMsgKeyBuilder;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.enums.UserTypeEnum;
import com.luohuo.flex.model.entity.dto.LoginMessageDTO;
import com.luohuo.flex.model.entity.dto.ScanSuccessMessageDTO;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.core.user.service.adapter.TextBuilder;
import com.luohuo.flex.im.core.user.service.adapter.UserAdapter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    @Value("${luohuo.wx.mp.callback}")
    private String callback;

    @Resource
    private UserDao userDao;

//    @Resource
//    private LoginService loginService;

	@Resource
	private CachePlusOps cachePlusOps;

    @Resource
    private MQProducer mqProducer;

    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage) {
        String openid = wxMpXmlMessage.getFromUser();
        Integer loginCode = Integer.parseInt(getEventKey(wxMpXmlMessage));
        User user = userDao.getByOpenId(openid);
        // 如果已经注册,直接登录成功
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getAvatar())) {
            if (Objects.equals(user.getUserType(), UserTypeEnum.BOT.getValue())) {
                return new TextBuilder().build("机器人账号不允许登录", wxMpXmlMessage, wxMpService);
            }
            mqProducer.sendMsg(MqConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), loginCode));
            return null;
        }

        // user为空先注册,手动生成,以保存uid
        if (Objects.isNull(user)) {
//            loginService.wxRegister(new RegisterReq(user.getName(), user.getAvatar(), user.getEmail(), user.getPassword(), user.getOpenId()));
        }
        // 在redis中保存openid和场景code的关系，后续才能通知到前端,旧版数据没有清除,这里设置了过期时间
		cachePlusOps.hSet(WxMsgKeyBuilder.builder(RedisKey.getKey(RedisKey.OPEN_ID_FORMAT, openid), 60L), loginCode);
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
        if (Objects.nonNull(user) && Objects.equals(user.getUserType(), UserTypeEnum.BOT.getValue())) {
            return;
        }
        // 更新用户信息
        if (StringUtils.isEmpty(user.getName())) {
            fillUserInfo(user.getId(), userInfo);
        }
        // 找到对应的code
		CacheResult<Integer> cacheResult = cachePlusOps.hGet(WxMsgKeyBuilder.builder(RedisKey.getKey(RedisKey.OPEN_ID_FORMAT, userInfo.getOpenid()), 60L));
		Integer code = cacheResult.getValue();
        // 发送登录成功事件
        mqProducer.sendMsg(MqConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), code));
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        // 基于用户ID生成11位纯数字账号
        User fillUser = UserAdapter.buildAuthorizeUser(uid, UUID.fastUUID().toString(), userInfo);

        try {
            userDao.updateById(fillUser);
        } catch (Exception e) {
            log.error("微信用户信息填充失败，uid:{}, info:{}", uid, userInfo, e);
            throw new RuntimeException("微信用户信息填充失败", e);
        }
    }
}
