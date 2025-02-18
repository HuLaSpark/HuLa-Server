package com.hula.common.event.listener;

import com.hula.common.event.MessageRecallEvent;
import com.hula.core.chat.domain.dto.ChatMsgRecallDTO;
import com.hula.core.chat.service.cache.MsgPlusCache;
import com.hula.core.user.service.adapter.WsAdapter;
import com.hula.core.user.service.impl.PushService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

/**
 * 消息撤回监听器
 *
 * @author nyh
 */
@Slf4j
@Component
public class MessageRecallListener {
    @Resource
    private MsgPlusCache msgPlusCache;
    @Resource
    private PushService pushService;

    @Async(HULA_EXECUTOR)
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
//        msgCache.evictMsg(recallDTO.getMsgId());
        msgPlusCache.delete(recallDTO.getMsgId());
    }

    @Async(HULA_EXECUTOR)
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        pushService.sendPushMsg(WsAdapter.buildMsgRecall(event.getRecallDTO()), event.getRecallDTO().getRecallUid());
    }

}
