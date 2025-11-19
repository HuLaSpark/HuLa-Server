package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.vo.response.msg.AudioCallMsgDTO;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.model.enums.CallStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
/**
 * 语音通话消息处理器
 */
@Component
public class AudioCallMsgHandler extends AbstractMsgHandler<AudioCallMsgDTO> {

	@Override
	MessageTypeEnum getMsgTypeEnum() {
		return MessageTypeEnum.AUDIO_CALL;
	}

	@Resource
	private MessageDao messageDao;
    @Override
    protected void saveMsg(Message message, AudioCallMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
		Message update = new Message();
        extra.setAudioCallMsgDTO(body);
		update.setId(message.getId());
		update.setExtra(extra);
		messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
		AudioCallMsgDTO dto = msg.getExtra().getAudioCallMsgDTO();
		Long uid = ContextUtil.getUid();

		// 自己的视角
		boolean isSender = uid.equals(msg.getFromUid());
		boolean isCreator = uid.equals(dto.getCreator());
		CallStatusEnum status = CallStatusEnum.of(dto.getState());

		// 统一处理双视角逻辑
		return switch (status) {
			case TIMEOUT ->
					isSender ? "对方未接听" : "未接听";
			case CANCEL ->
					(isSender == isCreator) ? "已取消" : "对方已取消";
			case REJECTED ->
					(isSender == isCreator) ? ("已" + status.getDesc()) : ("对方已" + status.getDesc());
			case ONGOING ->
					"通话进行中";
			case FAILED ->
					"连接失败";
			case RINGING ->
					"对方响铃中";
			default ->
					"通话时长 " + formatDuration(dto.getDuration());
		};
    }

    private String formatDuration(Long seconds) {
		if (seconds < 0) return "00:00";

		Duration duration = Duration.ofSeconds(seconds);
		long minutes = duration.toMinutes();
		seconds = duration.minusMinutes(minutes).getSeconds();
		return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "[语音通话]";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[语音通话]";
    }
}