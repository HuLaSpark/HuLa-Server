package com.luohuo.flex.im.core.chat.service;

import java.util.List;

/**
 * @author nyh
 */
public interface WeChatMsgOperationService {
    /**
     * 向被at的用户微信推送群聊消息
     *
     * @param senderUid senderUid
     * @param receiverUidList receiverUidList
     * @param msg msg
     */
    void publishChatMsgToWeChatUser(long senderUid, List<Long> receiverUidList, String msg);
}
