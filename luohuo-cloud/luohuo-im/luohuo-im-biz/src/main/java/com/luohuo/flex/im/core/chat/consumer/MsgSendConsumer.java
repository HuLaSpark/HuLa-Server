package com.luohuo.flex.im.core.chat.consumer;

import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.core.chat.dao.ContactDao;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.core.chat.dao.RoomDao;
import com.luohuo.flex.im.core.chat.dao.RoomFriendDao;
import com.luohuo.flex.im.domain.MsgSendMessageDTO;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.domain.entity.RoomFriend;
import com.luohuo.flex.im.domain.entity.msg.AudioCallMsgDTO;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.entity.msg.VideoCallMsgDTO;
import com.luohuo.flex.im.domain.enums.MessageTypeEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.ws.ChatMessageResp;
import com.luohuo.flex.im.core.chat.service.ChatService;
import com.luohuo.flex.im.core.chat.service.cache.GroupMemberCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomCache;
import com.luohuo.flex.im.core.user.service.adapter.WsAdapter;
import com.luohuo.flex.im.core.user.service.impl.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发送消息更新房间收信箱，并同步给房间成员信箱
 * @author 乾乾
 */
@Slf4j
@RocketMQMessageListener(consumerGroup = MqConstant.MSG_PUSH_OUTPUT_TOPIC_GROUP, topic = MqConstant.MSG_PUSH_OUTPUT_TOPIC, messageModel = MessageModel.CLUSTERING)
@Component
@AllArgsConstructor
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {

    private ChatService chatService;
    private MessageDao messageDao;
    private RoomCache roomCache;
    private RoomDao roomDao;
    private GroupMemberCache groupMemberCache;
    private RoomFriendDao roomFriendDao;
    private ContactDao contactDao;
    private PushService pushService;

    @Override
    public void onMessage(MsgSendMessageDTO dto) {
        Message message = messageDao.getById(dto.getMsgId());
        if (Objects.isNull(message)) {
            return;
        }
        Room room = roomCache.get(message.getRoomId());
        // 1. 所有房间更新房间最新消息
        roomDao.refreshActiveTime(room.getId(), message.getId(), message.getCreateTime());
        roomCache.refresh(room.getId());

		List<Long> memberUidList = new ArrayList<>();
		if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())) {
			memberUidList = groupMemberCache.getMemberExceptUidList(room.getId());
		} else if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())) {
			// 单聊对象, 对单人推送
			RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
			memberUidList.add(roomFriend.getUid1());
			memberUidList.add(roomFriend.getUid2());
		}

		// 2. 更新所有群成员的会话时间, 并推送房间成员
		refreshContactActiveTimeEnhanced(room.getId(), memberUidList, message.getId(), TimeUtils.getTime(message.getCreateTime()));

		// 3. 与在线人员交集并进行路由
		switch (MessageTypeEnum.of(message.getType())) {
			case AUDIO_CALL, VIDEO_CALL -> {
				Long uid = ContextUtil.getUid();

				// 3.1 给自己推送原始消息
				WsBaseResp<ChatMessageResp> selfResp = WsAdapter.buildMsgSend(chatService.getMsgResp(message, null));
				pushService.sendPushMsg(selfResp, uid, dto.getUid());

				// 3.2 修改消息发送者为通话创建者（用于其他人接收）
				Long originalFromUid = message.getFromUid();
				MessageExtra extra = message.getExtra();

				// 动态获取通话创建者
				Long creator = Optional.ofNullable(extra.getAudioCallMsgDTO())
						.map(AudioCallMsgDTO::getCreator)
						.orElseGet(() ->
								Optional.ofNullable(extra.getVideoCallMsgDTO())
										.map(VideoCallMsgDTO::getCreator)
										.orElse(originalFromUid)
						);
				message.setFromUid(creator);

				// 3.3 推送给其他成员
				List<Long> otherMembers = new ArrayList<>(memberUidList);
				otherMembers.remove(uid);

				WsBaseResp<ChatMessageResp> othersResp = WsAdapter.buildMsgSend(chatService.getMsgResp(message, null));
				// 恢复原始发送者显示
				othersResp.getData().getFromUser().setUid(originalFromUid + "");
				pushService.sendPushMsg(othersResp, otherMembers, dto.getUid());
				message.setFromUid(originalFromUid);
			}
			default -> {
				// 常规消息处理
				WsBaseResp<ChatMessageResp> wsBaseResp = WsAdapter.buildMsgSend(chatService.getMsgResp(message, null));
				pushService.sendPushMsg(wsBaseResp, memberUidList, dto.getUid());
			}
		}
    }

	// 增强版防抖控制
	private final Map<Long, DebounceInfo> roomDebounceInfo = new ConcurrentHashMap<>();
	private static final long MAX_DEBOUNCE_TIME = 10000;
	private static final int MAX_DEBOUNCE_COUNT = 1000;

	private void refreshContactActiveTimeEnhanced(Long roomId, List<Long> memberUidList, Long messageId, Long createTime) {
		Long currentTime = System.currentTimeMillis();
		DebounceInfo info = roomDebounceInfo.get(roomId);

		if (info == null) {
			// 首次更新
			contactDao.refreshOrCreateActiveTime(roomId, memberUidList, messageId, TimeUtils.timestampToLocalDateTime(createTime));
			roomDebounceInfo.put(roomId, new DebounceInfo(messageId, currentTime, 0));
			return;
		}

		// 检查是否需要立即更新
		boolean shouldUpdate = (currentTime - info.getLastUpdateTime()) >= MAX_DEBOUNCE_TIME || info.getPendingCount() >= MAX_DEBOUNCE_COUNT;

		if (shouldUpdate) {
			// 执行更新并使用最新消息ID
			contactDao.refreshOrCreateActiveTime(roomId, memberUidList, messageId, TimeUtils.timestampToLocalDateTime(createTime));
			roomDebounceInfo.put(roomId, new DebounceInfo(messageId, currentTime, 0));
			log.debug("强制更新会话时间 roomId: {}, 消息ID: {}", roomId, messageId);
		} else {
			// 累积防抖计数
			roomDebounceInfo.put(roomId, new DebounceInfo(messageId, info.getLastUpdateTime(), info.getPendingCount() + 1));
			log.debug("防抖中 roomId: {}, 累积消息: {}", roomId, info.getPendingCount() + 1);
		}
	}
}
