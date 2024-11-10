package com.hula.core.chat.service;

import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.core.chat.domain.dto.MsgReadInfoDTO;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.vo.request.*;
import com.hula.core.chat.domain.vo.request.member.MemberReq;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.chat.domain.vo.response.ChatMemberStatisticResp;
import com.hula.core.chat.domain.vo.response.ChatMessageReadResp;
import com.hula.core.chat.domain.vo.response.ChatMessageResp;
import com.hula.core.user.domain.vo.resp.ws.ChatMemberResp;
import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * 消息处理类
 * @author nyh
 */
public interface ChatService {

    /**
     * 发送消息
     */
    Long sendMsg(ChatMessageReq request, Long uid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param message 消息
     * @param receiveUid 接受消息的uid，可null
     */
    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param msgId 消息id
     * @param receiveUid 接受消息的uid，可null
     */
    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    /**
     * 获取群成员列表
     *
     * @param memberUidList 成员id集和
     * @param request 参数
     * @return {@link CursorPageBaseResp }<{@link ChatMemberResp }>
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request);

    /**
     * 获取消息列表
     *
     * @param request
     */
    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, @Nullable Long receiveUid);

    ChatMemberStatisticResp getMemberStatistic();

    void setMsgMark(Long uid, ChatMessageMarkReq request);

    void recallMsg(Long uid, ChatMessageBaseReq request);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq chatMessageMemberReq);

    Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request);

    CursorPageBaseResp<ChatMessageReadResp> getReadPage(Long uid, ChatMessageReadReq request);

    void msgRead(Long uid, ChatMessageMemberReq request);
}
