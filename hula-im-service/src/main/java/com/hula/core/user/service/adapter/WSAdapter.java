package com.hula.core.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.hula.core.chat.domain.dto.ChatMessageMarkDTO;
import com.hula.core.chat.domain.dto.ChatMsgRecallDTO;
import com.hula.core.chat.domain.vo.response.ChatMemberStatisticResp;
import com.hula.core.chat.domain.vo.response.ChatMessageResp;
import com.hula.core.chat.service.ChatService;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;
import com.hula.core.user.domain.enums.WSBaseResp;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.vo.resp.ws.*;
import jakarta.annotation.Resource;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * ws消息适配器
 * @author nyh
 */
@Component
public class WSAdapter {
    @Resource
    private ChatService chatService;

    public static WSBaseResp<WSLoginUrl> buildLoginResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_QR_CODE.getType());
        wsBaseResp.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        return wsBaseResp;
    }

    public static WSBaseResp<WSLoginSuccess> buildLoginSuccessResp(User user, String token, boolean hasPower) {
        WSBaseResp<WSLoginSuccess> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(hasPower ? 1 : 0)
                .build();
        wsBaseResp.setData(wsLoginSuccess);
        return wsBaseResp;
    }

    public static WSBaseResp buildScanSuccessResp() {
        WSBaseResp wsBaseResp = new WSBaseResp();
        wsBaseResp.setType(WSRespTypeEnum.WAITING_AUTHORIZE.getType());
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO recallDTO) {
        WSBaseResp<WSMsgRecall> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MSG_RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(recallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }

    public WSBaseResp<WSOnlineNotify> buildOnlineNotifyResp(User user) {
        WSBaseResp<WSOnlineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE.getType());
        WSOnlineNotify onlineOfflineNotify = new WSOnlineNotify();
        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOnlineInfo(user)));
        assembleNum(onlineOfflineNotify);
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }

    public WSBaseResp<WSOnlineNotify> buildOfflineNotifyResp(User user) {
        WSBaseResp<WSOnlineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.OFFLINE.getType());
        WSOnlineNotify onlineOfflineNotify = new WSOnlineNotify();
        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOfflineInfo(user)));
        assembleNum(onlineOfflineNotify);
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }

    private void assembleNum(WSOnlineNotify onlineOfflineNotify) {
        ChatMemberStatisticResp memberStatistic = chatService.getMemberStatistic();
        onlineOfflineNotify.setOnlineNum(memberStatistic.getOnlineNum());
    }

    private static ChatMemberResp buildOnlineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
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

    public static WSBaseResp<WsTokenExpire> buildInvalidateTokenResp(User user) {
        WSBaseResp<WsTokenExpire> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.TOKEN_EXPIRED.getType());
        WsTokenExpire wsTokenExpire = new WsTokenExpire();
        wsTokenExpire.setUid(user.getId());
        wsTokenExpire.setIp(user.getIpInfo().getCreateIp());
        wsBaseResp.setData(wsTokenExpire);
        return wsBaseResp;
    }

    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECEIVE_MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }

    public static WSBaseResp<WSMsgMark> buildMsgMarkSend(ChatMessageMarkDTO dto, Integer markCount) {
        WSMsgMark.WSMsgMarkItem item = new WSMsgMark.WSMsgMarkItem();
        BeanUtils.copyProperties(dto, item);
        item.setMarkCount(markCount);
        WSBaseResp<WSMsgMark> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MSG_MARK_ITEM.getType());
        WSMsgMark mark = new WSMsgMark();
        mark.setMarkList(Collections.singletonList(item));
        wsBaseResp.setData(mark);
        return wsBaseResp;
    }

    public static WSBaseResp<WSFriendApply> buildApplySend(WSFriendApply resp) {
        WSBaseResp<WSFriendApply> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.REQUEST_NEW_FRIEND.getType());
        wsBaseResp.setData(resp);
        return wsBaseResp;
    }
}
