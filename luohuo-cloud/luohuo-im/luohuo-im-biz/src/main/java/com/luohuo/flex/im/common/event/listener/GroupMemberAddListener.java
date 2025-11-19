package com.luohuo.flex.im.common.event.listener;

import com.luohuo.flex.common.OnlineService;
import com.luohuo.flex.im.common.event.GroupMemberAddEvent;
import com.luohuo.flex.im.core.chat.dao.GroupMemberDao;
import com.luohuo.flex.im.core.chat.service.adapter.MemberAdapter;
import com.luohuo.flex.im.core.chat.service.cache.GroupMemberCache;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.core.user.service.impl.PushService;
import com.luohuo.flex.model.entity.ws.ChatMember;
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

    private UserCache userCache;
	private GroupMemberDao groupMemberDao;
    private GroupMemberCache groupMemberCache;
	private OnlineService onlineService;
    private PushService pushService;

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
		List<Long> onlineUids = onlineService.getGroupOnlineMembers(roomId);
		Map<Long, User> map = userCache.getBatch(event.getMemberList());

		List<ChatMember> memberResps = groupMemberDao.getMemberListByUid(event.getMemberList());
		pushService.sendPushMsg(MemberAdapter.buildMemberAddWS(roomId, event.getTotalNum(), onlineUids, memberResps, map), memberUidList, event.getUid());

        // 移除缓存
		groupMemberCache.evictMemberList(roomId);
		groupMemberCache.evictExceptMemberList(roomId);
    }

}
