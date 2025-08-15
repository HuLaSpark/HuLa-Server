package com.luohuo.flex.im.core.chat.service;

import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.dto.MsgReadInfoDTO;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.vo.request.ChatMessageBaseReq;
import com.luohuo.flex.im.domain.vo.request.ChatMessageMarkReq;
import com.luohuo.flex.im.domain.vo.request.ChatMessageMemberReq;
import com.luohuo.flex.im.domain.vo.request.ChatMessagePageReq;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReadInfoReq;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReadReq;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberReq;
import com.luohuo.flex.im.domain.vo.response.ChatMessageReadResp;
import com.luohuo.flex.model.entity.ws.ChatMessageResp;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;
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
	 * 获取所有消息
	 */
	List<ChatMessageResp> getMsgList(Long lastOptTime, Long receiveUid);

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

    void setMsgMark(Long uid, ChatMessageMarkReq request);

    void recallMsg(Long uid, ChatMessageBaseReq request);

    Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request);

    CursorPageBaseResp<ChatMessageReadResp> getReadPage(Long uid, ChatMessageReadReq request);

    void msgRead(Long uid, ChatMessageMemberReq request);

	List<Message> getMsgByIds(List<Long> messageIds);
}
