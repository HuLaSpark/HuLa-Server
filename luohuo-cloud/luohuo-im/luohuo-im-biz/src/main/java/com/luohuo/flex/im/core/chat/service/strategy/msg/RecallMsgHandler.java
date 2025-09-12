package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.common.event.MessageRecallEvent;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.model.entity.dto.ChatMsgRecallDTO;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.entity.msg.MsgRecall;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.im.core.chat.service.cache.MsgCache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 撤回文本消息
 * @author nyh
 */
@Component
@AllArgsConstructor
public class RecallMsgHandler extends AbstractMsgHandler<Object> {

    private MessageDao messageDao;
	private UserSummaryCache userSummaryCache;
    private MsgCache msgCache;

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
		SummeryInfoDTO userInfo = userSummaryCache.get(recall.getRecallUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, List<Long> uidList, Message message) {//todo 消息覆盖问题用版本号解决
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid, TimeUtils.getTime(LocalDateTime.now())));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
		SpringUtils.publishEvent(new MessageRecallEvent(this, uidList, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));
    }

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
}
