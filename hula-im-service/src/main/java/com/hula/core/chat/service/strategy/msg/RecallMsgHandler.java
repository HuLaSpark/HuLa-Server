package com.hula.core.chat.service.strategy.msg;

import com.hula.common.event.MessageRecallEvent;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.dto.ChatMsgRecallDTO;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.entity.msg.MessageExtra;
import com.hula.core.chat.domain.entity.msg.MsgRecall;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import com.hula.core.chat.service.cache.MsgCache;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.service.cache.UserCache;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 撤回文本消息
 * @author nyh
 */
@Component
@AllArgsConstructor
public class RecallMsgHandler extends AbstractMsgHandler<Object> {

    private MessageDao messageDao;
    private UserCache userCache;
    private MsgCache msgCache;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public void saveMsg(Message msg, Object body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userCache.getUserInfo(recall.getRecallUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, Message message) {//todo 消息覆盖问题用版本号解决
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));

    }

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
}
