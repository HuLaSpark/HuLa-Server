package com.luohuo.flex.im.common.event.listener;

import com.luohuo.flex.im.common.event.MessageRecallEvent;
import com.luohuo.flex.model.entity.dto.ChatMsgRecallDTO;
import com.luohuo.flex.im.core.chat.service.cache.MsgCache;
import com.luohuo.flex.im.core.user.service.adapter.WsAdapter;
import com.luohuo.flex.im.core.user.service.impl.PushService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.luohuo.flex.im.common.config.ThreadPoolConfig.LUOHUO_EXECUTOR;

/**
 * 消息撤回监听器
 *
 * @author nyh
 */
@Slf4j
@Component
public class MessageRecallListener {
    @Resource
    private MsgCache msgCache;
    @Resource
    private PushService pushService;

    @Async(LUOHUO_EXECUTOR)
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
        msgCache.delete(recallDTO.getMsgId());
    }

    @Async(LUOHUO_EXECUTOR)
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        pushService.sendPushMsg(WsAdapter.buildMsgRecall(event.getRecallDTO()), event.getUidList(), event.getRecallDTO().getRecallUid());
    }

}
