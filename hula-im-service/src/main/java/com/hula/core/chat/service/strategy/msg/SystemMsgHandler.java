package com.hula.core.chat.service.strategy.msg;

import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 系统消息
 * @author nyh
 */
@Component
public class SystemMsgHandler extends AbstractMsgHandler<String> {

    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SYSTEM;
    }

    @Override
    public void saveMsg(Message msg, String body) {
        Message update = new Message();
        update.setId(msg.getId());
        update.setContent(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }
}
