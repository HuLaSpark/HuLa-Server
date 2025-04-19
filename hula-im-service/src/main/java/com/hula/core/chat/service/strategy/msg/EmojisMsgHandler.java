package com.hula.core.chat.service.strategy.msg;

import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.entity.msg.EmojisMsgDTO;
import com.hula.core.chat.domain.entity.msg.MessageExtra;
import com.hula.core.chat.domain.entity.msg.ReplyMsg;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 表情消息
 * @author nyh
 */
@Component
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {
    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.EMOJI;
    }

    @Override
    public void saveMsg(Message msg, EmojisMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
		update.setReplyMsgId(body.getReplyMsgId());
        extra.setEmojisMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
		EmojisMsgDTO resp = msg.getExtra().getEmojisMsgDTO();
		resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
		resp.setReply(replyMsg(msg));
		return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "表情";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[动画表情]";
    }
}
