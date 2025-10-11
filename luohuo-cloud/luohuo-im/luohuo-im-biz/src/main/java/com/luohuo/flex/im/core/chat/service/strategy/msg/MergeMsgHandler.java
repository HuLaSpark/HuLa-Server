package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.core.chat.service.cache.MsgCache;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.vo.response.msg.MergeMsgDTO;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.luohuo.flex.im.domain.enums.MessageTypeEnum.*;

/**
 * 合并消息
 * @author 乾乾
 */
@Component
@AllArgsConstructor
public class MergeMsgHandler extends AbstractMsgHandler<MergeMsgDTO> {
    private MsgCache msgCache;
	private UserCache userCache;
	private MessageDao messageDao;

	@Override
    MessageTypeEnum getMsgTypeEnum() {
        return MERGE;
    }

	@Override
	protected void saveMsg(Message message, MergeMsgDTO body) {
		List<Message> messages = new ArrayList<>(msgCache.getBatch(
					body.getBody().stream()
							.limit(3)
							.map(item -> Long.parseLong(item.getMessageId()))
							.collect(Collectors.toList())).values().stream()
			.sorted(Comparator.comparing(Message::getId))
			.collect(Collectors.toList())
		);

		List<String> content = messages.stream().map(msg ->
			userCache.get(msg.getFromUid()).getName() + ": " + switch (MessageTypeEnum.of(msg.getType())) {
				case MERGE -> "[聊天记录]";
				case SOUND -> "[语音]";
				case VIDEO -> "[视频]";
				case FILE -> "[文件]";
				case IMG -> "[图片]";
				case RECALL -> "[消息已撤回]";
				case EMOJI -> "[表情]";
				case NOTICE -> "[公告消息]";
				default -> msg.getContent();
			}).collect(Collectors.toList());
		body.setContent(content);

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
