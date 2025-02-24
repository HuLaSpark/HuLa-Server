package com.hula.core.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.hula.core.chat.domain.dto.ChatMessageMarkDTO;
import com.hula.core.chat.domain.dto.ChatMsgRecallDTO;
import com.hula.core.chat.domain.vo.response.ChatMessageResp;
import com.hula.core.user.domain.entity.IpDetail;
import com.hula.core.user.domain.entity.IpInfo;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.resp.user.OffLineResp;
import com.hula.core.user.domain.vo.resp.ws.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

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

    public WsBaseResp<WSOnlineNotify> buildOnlineNotifyResp(User user, Long onlineNum) {
        WsBaseResp<WSOnlineNotify> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE.getType());
        WSOnlineNotify onlineOfflineNotify = new WSOnlineNotify();
        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOnlineInfo(user)));
        onlineOfflineNotify.setOnlineNum(onlineNum);
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }

    public WsBaseResp<WSOnlineNotify> buildOfflineNotifyResp(User user, Long onlineNum) {
        WsBaseResp<WSOnlineNotify> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.OFFLINE.getType());
        WSOnlineNotify onlineOfflineNotify = new WSOnlineNotify();
        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOfflineInfo(user)));
        onlineOfflineNotify.setOnlineNum(onlineNum);
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }

    private static ChatMemberResp buildOnlineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
		info.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
        info.setLastOptTime(user.getLastOptTime());
        return info;
    }

    private static ChatMemberResp buildOfflineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        info.setLastOptTime(user.getLastOptTime());
        return info;
    }

    public static WsBaseResp<OffLineResp> buildInvalidateTokenResp(OffLineResp offLineResp) {
        WsBaseResp<OffLineResp> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.TOKEN_EXPIRED.getType());
        wsBaseResp.setData(offLineResp);
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
        BeanUtils.copyProperties(dto, item);
        item.setMarkCount(markCount);
        WsBaseResp<WSMsgMark> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MSG_MARK_ITEM.getType());
        WSMsgMark mark = new WSMsgMark();
        mark.setMarkList(Collections.singletonList(item));
        wsBaseResp.setData(mark);
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

}
