package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.msg.MergeMsgDTO;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 合并消息
 * @author 乾乾
 */
@Component
@AllArgsConstructor
public class MergeMsgHandler extends AbstractMsgHandler<MergeMsgDTO> {
    private MessageDao messageDao;

	@Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.MERGE;
    }

	@Override
	protected void saveMsg(Message message, MergeMsgDTO body) {
		MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
		Message update = new Message();
		update.setId(message.getId());
		extra.setMergeMsgDTO(body);
		update.setExtra(extra);
		update.setReplyMsgId(body.getReplyMsgId());
		messageDao.updateById(update);
	}

	@Override
    public Object showMsg(Message msg) {
		MergeMsgDTO resp = msg.getExtra().getMergeMsgDTO();
		resp.setReply(replyMsg(msg));
		return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
		return "合并消息";
    }

    @Override
    public String showContactMsg(Message msg) {
		return "[合并消息]";
    }
}
