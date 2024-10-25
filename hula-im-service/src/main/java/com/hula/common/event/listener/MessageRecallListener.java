package com.hula.common.event.listener;

import com.hula.common.event.MessageRecallEvent;
import com.hula.core.chat.domain.dto.ChatMsgRecallDTO;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.cache.MsgCache;
import com.hula.core.chat.service.cache.MsgPlusCache;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.adapter.WSAdapter;
import com.hula.core.user.service.impl.PushService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 消息撤回监听器
 *
 * @author nyh
 */
@Slf4j
@Component
public class MessageRecallListener {
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private ChatService chatService;
    @Resource
    private MsgCache msgCache;
    @Resource
    private MsgPlusCache msgPlusCache;
    @Resource
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
//        msgCache.evictMsg(recallDTO.getMsgId());
        msgPlusCache.delete(recallDTO.getMsgId());
    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        pushService.sendPushMsg(WSAdapter.buildMsgRecall(event.getRecallDTO()));
    }

}
