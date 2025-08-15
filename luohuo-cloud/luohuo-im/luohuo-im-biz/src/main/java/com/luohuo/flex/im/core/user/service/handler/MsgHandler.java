package com.luohuo.flex.im.core.user.service.handler;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.im.core.chat.dao.WxMsgDao;
import com.luohuo.flex.im.domain.entity.WxMsg;
import com.luohuo.flex.im.core.user.service.adapter.TextBuilder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author nyh
 */
@Component
@Slf4j
public class MsgHandler extends AbstractHandler {

    @Resource
    private WxMsgDao wxMsgDao;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {
        boolean flag = true;
        if (flag) {
            WxMsg msg = new WxMsg();
            msg.setOpenId(wxMessage.getFromUser());
            msg.setMsg(wxMessage.getContent());
            wxMsgDao.save(msg);
            return null;
        }
        if (!wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
            // 可以选择将消息保存到本地
        }

        // 当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                    && !weixinService.getKefuService().kfOnlineList()
                    .getKfOnlineList().isEmpty()) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
        }

        // 组装回复消息
        String content = "收到信息内容：" + JSONUtil.toJsonStr(wxMessage);

        return new TextBuilder().build(content, wxMessage, weixinService);

    }

}
