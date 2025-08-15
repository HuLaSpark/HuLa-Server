package com.luohuo.flex.im.common.event.listener;

import com.luohuo.flex.im.api.PresenceApi;
import com.luohuo.flex.im.common.event.GroupMemberAddEvent;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReq;
import com.luohuo.flex.im.core.chat.service.ChatService;
import com.luohuo.flex.im.core.chat.service.adapter.MemberAdapter;
import com.luohuo.flex.im.core.chat.service.adapter.RoomAdapter;
import com.luohuo.flex.im.core.chat.service.cache.GroupMemberCache;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.core.user.service.cache.UserInfoCache;
import com.luohuo.flex.im.core.user.service.impl.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.List;
import java.util.Map;

import static com.luohuo.flex.im.common.config.ThreadPoolConfig.LUOHUO_EXECUTOR;

/**
 * 添加群成员监听器
 *
 * @author nyh
 */
@Slf4j
@Component
@AllArgsConstructor
public class GroupMemberAddListener {

    private ChatService chatService;
    private UserInfoCache userInfoCache;
    private GroupMemberCache groupMemberCache;
	private PresenceApi presenceApi;
    private PushService pushService;

	/**
	 * 触发群主邀请群员的消息
	 * @param event
	 */
    @Async(LUOHUO_EXECUTOR)
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true, phase = TransactionPhase.AFTER_COMMIT)
    public void sendAddMsg(GroupMemberAddEvent event) {
		List<Long> uidList = event.getMemberList();
		Long roomId = event.getRoomId();
		User user = userInfoCache.get(event.getCUid());
		ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomId, user, userInfoCache.getBatch(uidList));
		chatService.sendMsg(chatMessageReq, user.getId());
    }

	/**
	 * TODO 这里要做屏蔽群成员、所有群成员
	 * 群成员变动推送逻辑
	 * @param event
	 */
    @Async(LUOHUO_EXECUTOR)
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true, phase = TransactionPhase.AFTER_COMMIT)
    public void sendChangePush(GroupMemberAddEvent event) {
        Long roomId = event.getRoomId();
        List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomId);
		// 在线的用户
		List<Long> onlineUids = presenceApi.getGroupOnlineMembers(roomId).getData();
		Map<Long, User> map = userInfoCache.getBatch(event.getMemberList());
		pushService.sendPushMsg(MemberAdapter.buildMemberAddWS(roomId, onlineUids, map), memberUidList, event.getCUid());

        // 移除缓存
        groupMemberCache.evictMemberUidList(roomId);
    }

}
