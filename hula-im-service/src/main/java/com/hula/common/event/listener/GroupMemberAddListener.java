package com.hula.common.event.listener;

import com.hula.common.event.GroupMemberAddEvent;
import com.hula.core.chat.dao.GroupMemberDao;
import com.hula.core.chat.domain.entity.GroupMember;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.vo.request.ChatMessageReq;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.adapter.MemberAdapter;
import com.hula.core.chat.service.adapter.RoomAdapter;
import com.hula.core.chat.service.cache.GroupMemberCache;
import com.hula.core.chat.service.cache.MsgCache;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.WSBaseResp;
import com.hula.core.user.domain.vo.resp.ws.WSMemberChange;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.cache.UserInfoCache;
import com.hula.core.user.service.impl.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 添加群成员监听器
 *
 * @author nyh
 */
@Slf4j
@Component
@AllArgsConstructor
public class GroupMemberAddListener {

    private WebSocketService webSocketService;
    private ChatService chatService;
    private MsgCache msgCache;
    private UserInfoCache userInfoCache;
    private ApplicationEventPublisher applicationEventPublisher;
    private GroupMemberDao groupMemberDao;
    private UserDao userDao;
    private GroupMemberCache groupMemberCache;
    private PushService pushService;


    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        Long inviteUid = event.getInviteUid();
        User user = userInfoCache.get(inviteUid);
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, userInfoCache.getBatch(uidList));
        chatService.sendMsg(chatMessageReq, User.UID_SYSTEM);
    }

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendChangePush(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<User> users = userDao.listByIds(uidList);
        users.forEach(user -> {
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(), user);
            pushService.sendPushMsg(ws, memberUidList);
        });
        //移除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }

}
