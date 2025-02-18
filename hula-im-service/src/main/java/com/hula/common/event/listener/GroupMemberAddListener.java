package com.hula.common.event.listener;

import com.hula.common.event.GroupMemberAddEvent;
import com.hula.core.chat.domain.entity.GroupMember;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.vo.request.ChatMessageReq;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.adapter.MemberAdapter;
import com.hula.core.chat.service.adapter.RoomAdapter;
import com.hula.core.chat.service.cache.GroupMemberCache;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.resp.ws.WSMemberChange;
import com.hula.core.user.service.cache.UserInfoCache;
import com.hula.core.user.service.impl.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.List;
import java.util.stream.Collectors;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

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
    private UserDao userDao;
    private GroupMemberCache groupMemberCache;
    private PushService pushService;


	/**
	 * 触发群主邀请群员的消息
	 * @param event
	 */
    @Async(HULA_EXECUTOR)
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true, phase = TransactionPhase.AFTER_COMMIT)
    public void sendAddMsg(GroupMemberAddEvent event) {
		List<GroupMember> memberList = event.getMemberList();
		RoomGroup roomGroup = event.getRoomGroup();
		Long inviteUid = event.getInviteUid();
		User user = userInfoCache.get(inviteUid);
		List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
		ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, userInfoCache.getBatch(uidList));
		chatService.sendMsg(chatMessageReq, user.getId());
    }

	/**
	 *
	 * @param event
	 */
    @Async(HULA_EXECUTOR)
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true, phase = TransactionPhase.AFTER_COMMIT)
    public void sendChangePush(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<User> users = userDao.listByIds(uidList);
        users.forEach(user -> {
            WsBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(), user);
            pushService.sendPushMsg(ws, memberUidList, event.getInviteUid());
        });
        // 移除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }

}
