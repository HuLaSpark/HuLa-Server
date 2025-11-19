package com.luohuo.flex.im.core.chat.consumer;

import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.common.OnlineService;
import com.luohuo.flex.common.cache.PassageMsgCacheKeyBuilder;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.core.chat.dao.RoomDao;
import com.luohuo.flex.im.core.chat.dao.RoomFriendDao;
import com.luohuo.flex.im.domain.MsgSendMessageDTO;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.domain.entity.RoomFriend;
import com.luohuo.flex.im.domain.vo.response.msg.AudioCallMsgDTO;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import com.luohuo.flex.im.domain.vo.response.msg.VideoCallMsgDTO;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 发送消息更新房间收信箱，并同步给房间成员信箱
 * 加入 在途消息缓存，三秒没有回执则再次推送
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
	private OnlineService onlineService;
    private PushService pushService;
	private CachePlusOps cachePlusOps;

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

		// 2. 过滤出在线的人员
		Set<Long> onlineUsersList = onlineService.getOnlineUsersList(memberUidList);

		// 3. 与在线人员交集并进行路由
		switch (MessageTypeEnum.of(message.getType())) {
			case AUDIO_CALL, VIDEO_CALL -> {
				Long uid = ContextUtil.getUid();

				// 3.1 给自己推送原始消息
				WsBaseResp<ChatMessageResp> selfResp = WsAdapter.buildMsgSend(chatService.getMsgResp(message, null));
				pushService.sendPushMsg(selfResp, uid, dto.getUid());
				asyncSavePassageMsg(message.getId(), selfResp, Set.of(uid), dto.getUid());

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
				onlineUsersList.remove(uid);
				List<Long> otherMembers = new ArrayList<>(onlineUsersList);

				WsBaseResp<ChatMessageResp> othersResp = WsAdapter.buildMsgSend(chatService.getMsgResp(message, null));
				// 恢复原始发送者显示
				othersResp.getData().getFromUser().setUid(originalFromUid + "");
				pushService.sendPushMsg(othersResp, otherMembers, dto.getUid());
				asyncSavePassageMsg(message.getId(), othersResp, onlineUsersList, dto.getUid());
				message.setFromUid(originalFromUid);
			}
			default -> {
				// 常规消息处理
				WsBaseResp<ChatMessageResp> wsBaseResp = WsAdapter.buildMsgSend(chatService.getMsgResp(message, null));
				pushService.sendPushMsg(wsBaseResp, new ArrayList<>(onlineUsersList), dto.getUid());
				asyncSavePassageMsg(message.getId(), wsBaseResp, onlineUsersList, dto.getUid());
			}
		}
    }

	/**
	 * 这里理论上一定会比 pushService 到达前端后在ack给后端 先执行完成
	 * @param messageId 消息的id [唯一标识，未来升级为hash之后的值]
	 * @param wsBaseResp 推送的消息
	 * @param memberUidList 推送的列表
	 * @param cuid 操作人
	 */
	@Async
	public void asyncSavePassageMsg(Long messageId, WsBaseResp<?> wsBaseResp, Set<Long> memberUidList, Long cuid) {
		// 1. 发送重试消息
		pushService.sendPushMsgWithRetry(wsBaseResp, new ArrayList<>(memberUidList), messageId, cuid);

		// 2. 给每条消息加入 在途的状态
		memberUidList.forEach(memberUid -> {
			// 添加在途消息
			cachePlusOps.sAdd(PassageMsgCacheKeyBuilder.build(memberUid), messageId);
		});
	}

}
