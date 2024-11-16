package com.hula.common.event.listener;

import com.hula.common.enums.IdempotentEnum;
import com.hula.common.event.MessageMarkEvent;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.dao.MessageMarkDao;
import com.hula.core.chat.domain.dto.ChatMessageMarkDTO;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.enums.MessageMarkTypeEnum;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import com.hula.core.user.domain.enums.ItemEnum;
import com.hula.core.user.service.UserBackpackService;
import com.hula.core.user.service.adapter.WSAdapter;
import com.hula.core.user.service.impl.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * 消息标记监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
@AllArgsConstructor
public class MessageMarkListener {

    private MessageMarkDao messageMarkDao;
    private MessageDao messageDao;
    private UserBackpackService userBackpackService;
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void changeMsgType(MessageMarkEvent event) {
        ChatMessageMarkDTO dto = event.getDto();
        Message msg = messageDao.getById(dto.getMsgId());
        if (!Objects.equals(msg.getType(), MessageTypeEnum.TEXT.getType())) {
            //普通消息才需要升级
            return;
        }
        //消息被标记次数
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(dto.getMarkType());
        if (markCount < markTypeEnum.getRiseNum()) {
            return;
        }
        if (MessageMarkTypeEnum.LIKE.getType().equals(dto.getMarkType())) {
            //尝试给用户发送一张徽章
            userBackpackService.acquireItem(msg.getFromUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentEnum.MSG_ID, msg.getId().toString());
        }
    }

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void notifyAll(MessageMarkEvent event) {//后续可做合并查询，目前异步影响不大
        ChatMessageMarkDTO dto = event.getDto();
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        pushService.sendPushMsg(WSAdapter.buildMsgMarkSend(dto, markCount), dto.getUid());
    }

}
