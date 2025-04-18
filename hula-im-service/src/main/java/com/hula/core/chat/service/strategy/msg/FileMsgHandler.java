package com.hula.core.chat.service.strategy.msg;

import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.entity.msg.FileMsgDTO;
import com.hula.core.chat.domain.entity.msg.MessageExtra;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 图片消息
 * @author nyh
 */
@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO> {
    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.FILE;
    }

    @Override
    public void saveMsg(Message msg, FileMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
		update.setReplyMsgId(body.getReplyMsgId());
        extra.setFileMsg(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
		FileMsgDTO resp = msg.getExtra().getFileMsg();
		resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
		resp.setReply(replyMsg(msg));
		return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "文件:" + msg.getExtra().getFileMsg().getFileName();
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[文件]" + msg.getExtra().getFileMsg().getFileName();
    }
}
