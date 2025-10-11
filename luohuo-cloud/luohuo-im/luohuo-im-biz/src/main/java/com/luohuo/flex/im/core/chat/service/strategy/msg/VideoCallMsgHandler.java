package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.vo.response.msg.VideoCallMsgDTO;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.model.enums.CallStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * 视频通话消息处理器
 */
@Component
public class VideoCallMsgHandler extends AbstractMsgHandler<VideoCallMsgDTO> {

	@Resource
	private UserSummaryCache userSummaryCache;

	@Override
	MessageTypeEnum getMsgTypeEnum() {
		return MessageTypeEnum.VIDEO_CALL;
	}

	@Resource
	private MessageDao messageDao;
    @Override
    protected void saveMsg(Message message, VideoCallMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
		Message update = new Message();
        extra.setVideoCallMsgDTO(body);
		update.setId(message.getId());
		update.setExtra(extra);
		messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
		VideoCallMsgDTO dto = msg.getExtra().getVideoCallMsgDTO();

		// ===== 群聊场景 =====
		if (Boolean.TRUE.equals(dto.getIsGroup())) {
			return dto.getBegin() ? userSummaryCache.get(msg.getFromUid()).getName() + "发起了视频通话" : "视频通话已结束";
		}

		// ===== 私聊场景 =====

		// 结束消息状态处理
		Long uid = ContextUtil.getUid();
		boolean isSender = uid.equals(msg.getFromUid());
		CallStatusEnum status = CallStatusEnum.of(dto.getState());
		if (status == null) return "通话状态未知";

		// 双视角统一规则
		return switch (status) {
			case REJECTED ->
					!isSender ? "已" + status.getDesc() : "对方已" + status.getDesc();
			case TIMEOUT ->
					isSender ? "对方未接听" : "未接听";
			case CANCEL ->
					isSender ? "已取消" : "对方已取消";
			case ONGOING ->
					"通话进行中";
			case FAILED ->
					"连接失败";
			case RINGING ->
					"房间响铃中";
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
        return "[视频通话]";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[视频通话]";
    }
}