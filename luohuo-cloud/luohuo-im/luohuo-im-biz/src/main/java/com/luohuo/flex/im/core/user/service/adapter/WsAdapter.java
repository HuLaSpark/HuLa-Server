package com.luohuo.flex.im.core.user.service.adapter;

import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.im.domain.vo.req.room.UserApplyResp;
import com.luohuo.flex.model.entity.ws.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.domain.dto.ChatMessageMarkDTO;
import com.luohuo.flex.model.entity.dto.ChatMsgRecallDTO;
import com.luohuo.flex.im.domain.vo.request.contact.ContactNotificationReq;
import com.luohuo.flex.model.entity.ws.ChatMessageResp;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * ws消息适配器
 * @author nyh
 */
@Component
public class WsAdapter {

    public static WsBaseResp<WSLoginUrl> buildLoginResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WsBaseResp<WSLoginUrl> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_QR_CODE.getType());
        wsBaseResp.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        return wsBaseResp;
    }

    public static WsBaseResp<WSLoginSuccess> buildLoginSuccessResp(User user, String token, String refreshToken, boolean hasPower) {
        WsBaseResp<WSLoginSuccess> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
				.refreshToken(refreshToken)
                .uid(user.getId())
                .power(hasPower ? 1 : 0)
                .build();
        wsBaseResp.setData(wsLoginSuccess);
        return wsBaseResp;
    }

    public static WsBaseResp<?> buildScanSuccessResp() {
        WsBaseResp<?> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.WAITING_AUTHORIZE.getType());
        return wsBaseResp;
    }

    public static WsBaseResp<?> buildMsgRecall(ChatMsgRecallDTO recallDTO) {
        WsBaseResp<WSMsgRecall> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MSG_RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(recallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }

    public static WsBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WsBaseResp<ChatMessageResp> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECEIVE_MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }

    public static WsBaseResp<WSMsgMark> buildMsgMarkSend(ChatMessageMarkDTO dto, Integer markCount) {
        WSMsgMark.WSMsgMarkItem item = new WSMsgMark.WSMsgMarkItem();
		item.setMsgId(dto.getMsgId()+"");
		item.setUid(dto.getUid()+"");
        item.setMarkCount(markCount);
		item.setMarkType(dto.getMarkType());
		item.setActType(dto.getActType());

        WsBaseResp<WSMsgMark> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MSG_MARK_ITEM.getType());
        wsBaseResp.setData(new WSMsgMark(Collections.singletonList(item)));
        return wsBaseResp;
    }

    public static WsBaseResp<WSFriendApply> buildApplySend(WSFriendApply resp) {
        WsBaseResp<WSFriendApply> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.REQUEST_NEW_FRIEND.getType());
        wsBaseResp.setData(resp);
        return wsBaseResp;
    }

    public static WsBaseResp<WSFriendApproval> buildApprovalSend(WSFriendApproval resp) {
        WsBaseResp<WSFriendApproval> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.REQUEST_APPROVAL_FRIEND.getType());
        wsBaseResp.setData(resp);
        return wsBaseResp;
    }
	public static WsBaseResp<ContactNotificationReq> buildContactNotification(ContactNotificationReq req) {
		WsBaseResp<ContactNotificationReq> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.ROOM_NOTIFICATION.getType());
		wsBaseResp.setData(req);
		return wsBaseResp;
	}

	/**
	 * 屏蔽/解除屏蔽好友/群
	 * @param name
	 */
	public static WsBaseResp<String> buildShieldContact(Boolean state, String name) {
		WsBaseResp<String> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(state? WSRespTypeEnum.SHIELD.getType(): WSRespTypeEnum.UNBLOCK.getType());
		wsBaseResp.setData(StrUtil.format("你已{}屏蔽来自{}的消息", state? "":"解除",name));
		return wsBaseResp;
	}

	public static WsBaseResp<UserApplyResp> buildApplyResultWS(Long uid, Long roomId, Long targetId, String msg, Integer status, Integer readStatus) {
		WsBaseResp<UserApplyResp> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.GROUP_APPLY_NOTICE.getType());
		wsBaseResp.setData(new UserApplyResp(uid, 2, roomId, targetId, msg, status, readStatus, LocalDateTime.now()));
		return wsBaseResp;
	}
}
