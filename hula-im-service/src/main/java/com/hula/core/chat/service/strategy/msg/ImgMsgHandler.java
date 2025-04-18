package com.hula.core.chat.service.strategy.msg;

import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.domain.entity.msg.ImgMsgDTO;
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
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {
    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    public void saveMsg(Message msg, ImgMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
		update.setReplyMsgId(body.getReplyMsgId());
        extra.setImgMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
		ImgMsgDTO resp = msg.getExtra().getImgMsgDTO();
		resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
		resp.setReply(replyMsg(msg));
		return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "图片";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[图片]";
    }
}
