package com.hula.core.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import com.hula.common.annotation.RedissonLock;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.event.MessageSendEvent;
import com.hula.core.chat.dao.*;
import com.hula.core.chat.domain.dto.ChatMsgSendDto;
import com.hula.core.chat.domain.dto.MsgReadInfoDTO;
import com.hula.core.chat.domain.entity.*;
import com.hula.core.chat.domain.enums.MessageMarkActTypeEnum;
import com.hula.core.chat.domain.enums.MessageTypeEnum;
import com.hula.core.chat.domain.enums.RoomTypeEnum;
import com.hula.core.chat.domain.vo.request.*;
import com.hula.core.chat.domain.vo.request.member.MemberReq;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.chat.domain.vo.response.ChatMemberStatisticResp;
import com.hula.core.chat.domain.vo.response.ChatMessageReadResp;
import com.hula.core.chat.domain.vo.response.ChatMessageResp;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.ContactService;
import com.hula.core.chat.service.adapter.MemberAdapter;
import com.hula.core.chat.service.adapter.MessageAdapter;
import com.hula.core.chat.service.adapter.RoomAdapter;
import com.hula.core.chat.service.cache.RoomCache;
import com.hula.core.chat.service.cache.RoomGroupCache;
import com.hula.core.chat.service.helper.ChatMemberHelper;
import com.hula.core.chat.service.strategy.mark.AbstractMsgMarkStrategy;
import com.hula.core.chat.service.strategy.mark.MsgMarkFactory;
import com.hula.core.chat.service.strategy.msg.AbstractMsgHandler;
import com.hula.core.chat.service.strategy.msg.MsgHandlerFactory;
import com.hula.core.chat.service.strategy.msg.RecallMsgHandler;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.dao.UserFriendDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.entity.UserFriend;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;
import com.hula.core.user.domain.enums.RoleTypeEnum;
import com.hula.core.user.domain.vo.resp.ws.ChatMemberResp;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.cache.UserCache;
import com.hula.exception.BizException;
import com.hula.utils.AssertUtil;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;


/**
 * 消息处理类
 *
 * @author nyh
 */
@Service
@Slf4j
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
    public static final long ROOM_GROUP_ID = 1L;
    private final RoomDao roomDao;
    private final UserFriendDao userFriendDao;
    private final RoomGroupDao roomGroupDao;

    private MessageDao messageDao;
    private UserDao userDao;
    private ApplicationEventPublisher applicationEventPublisher;
    private UserCache userCache;
    private MessageMarkDao messageMarkDao;
    private RoomFriendDao roomFriendDao;
    private RoleService roleService;
    private RecallMsgHandler recallMsgHandler;
    private ContactService contactService;
    private ContactDao contactDao;
    private RoomCache roomCache;
    private GroupMemberDao groupMemberDao;
    private RoomGroupCache roomGroupCache;

    /**
     * 发送消息
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        check(request.getSkip(), request.getIsTemp(), request.getRoomId(), uid);
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());
        Long msgId = msgHandler.checkAndSaveMsg(request, uid);

		// 临时会话单独处理一下消息计数器
		if (request.getIsTemp()) {
			Room room = roomCache.get(request.getRoomId());
			if(room != null && room.isRoomFriend() && !request.getIsPushMessage()){
				UserFriend userFriend = userFriendDao.getByRoomId(request.getRoomId(), uid);
				if (userFriend != null && userFriend.getIsTemp()) {
					userFriend.setTempMsgCount(userFriend.getTempMsgCount() + 1);
					userFriendDao.updateById(userFriend);
				}
			}
		}

        // 发布消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, new ChatMsgSendDto(msgId, uid)));
        return msgId;
    }

    private void checkDeFriend(Boolean isTemp, Long roomId, Long uid) {
        Room room = roomCache.get(roomId);
        Assert.notNull(room, "房间不存在!");
        if (room.isRoomGroup()) {
            RoomGroup roomGroup = roomGroupCache.get(roomId);
            GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);
            Assert.notNull(member, "您已经被移除该群");
            Assert.isFalse(member.getDeFriend(), "您已经屏蔽群聊!");
        } else {
            RoomFriend roomFriend = roomFriendDao.getByRoomId(roomId);
            boolean u1State = uid.equals(roomFriend.getUid1());
            boolean u2State = uid.equals(roomFriend.getUid2());

			if(isTemp){
				// 临时会话的消息校验，整合 UserFriend、RoomFriend 的功能
				UserFriend userFriend = userFriendDao.getByRoomId(roomId, uid);
				if (userFriend == null || !userFriend.getIsTemp()) {
					throw new BizException("当前会话不存在或不是临时会话");
				}

				if (uid.equals(roomFriend.getUid1()) && !userFriend.getTempStatus() && userFriend.getTempMsgCount() >= 1) {
					throw new BizException("对方未回复前只能发送一条打招呼信息");
				}

				if (uid.equals(userFriend.getUid()) && userFriend.getTempMsgCount() >= 5) {
					throw new BizException("临时会话最多只能发送5条消息");
				}
			}
			if (roomFriend.getDeFriend1() && u1State || roomFriend.getDeFriend2() && u2State) {
				throw new BizException("已屏蔽对方!");
			}

            Assert.isTrue(u1State || u2State, "消息已发送, 但对方拒收了");
        }
    }

	/**
	 * @param skip 是否跳过检查
	 * @param isTemp 当前检查的会话是否是临时会话【特殊处理】
	 * @param roomId 房间id
	 * @param uid 当前登录用户id
	 */
    private void check(Boolean skip, Boolean isTemp, Long roomId, Long uid) {
        if (skip) {
            return;
        }
        checkDeFriend(isTemp, roomId, uid);
    }

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(message.getRoomId(), Collections.singletonList(message), receiveUid));
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        Message msg = messageDao.getById(msgId);
        return getMsgResp(msg, receiveUid);
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request) {
        Pair<ChatActiveStatusEnum, String> pair = ChatMemberHelper.getCursorPair(request.getCursor());
        ChatActiveStatusEnum activeStatusEnum = pair.getKey();
        String timeCursor = pair.getValue();
        // 最终列表
        List<ChatMemberResp> resultList = new ArrayList<>();
        Boolean isLast = Boolean.FALSE;
        if (activeStatusEnum == ChatActiveStatusEnum.ONLINE) {
            // 在线列表
            CursorPageBaseResp<User> cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(request.getPageSize(), timeCursor), ChatActiveStatusEnum.ONLINE);
            // 添加在线列表
            resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));
            if (cursorPage.getIsLast()) {
                // 如果是最后一页,从离线列表再补点数据
                activeStatusEnum = ChatActiveStatusEnum.OFFLINE;
                Integer leftSize = request.getPageSize() - cursorPage.getList().size();
                cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(leftSize, null), ChatActiveStatusEnum.OFFLINE);
                // 添加离线线列表
                resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));
            }
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        } else if (activeStatusEnum == ChatActiveStatusEnum.OFFLINE) {
            // 离线列表
            CursorPageBaseResp<User> cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(request.getPageSize(), timeCursor), ChatActiveStatusEnum.OFFLINE);
            // 添加离线线列表
            resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        }
        // 获取群成员角色ID
        List<Long> uidList = resultList.stream().map(item -> Long.parseLong(item.getUid())).collect(Collectors.toList());
        RoomGroup roomGroup = roomGroupCache.getByRoomId(request.getRoomId());
        Map<String, Integer> uidMapRole = groupMemberDao.getMemberMapRole(roomGroup.getId(), uidList);
        resultList.forEach(member -> member.setRoleId(uidMapRole.get(member.getUid())));
        // 组装结果
        return new CursorPageBaseResp<>(ChatMemberHelper.generateCursor(activeStatusEnum, timeCursor),
                isLast, resultList, groupMemberDao.lambdaQuery().eq(GroupMember::getGroupId, roomGroup.getId()).count());
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long receiveUid) {
        // 1. 用最后一条消息id，来限制被踢出的人能看见的最大一条消息
        Long lastMsgId = getLastMsgId(request.getRoomId(), receiveUid);
        // 2. 判断我屏蔽会话没有权限
        check(request.getSkip(), false, request.getRoomId(), receiveUid);
        CursorPageBaseResp<Message> cursorPage = messageDao.getCursorPage(request.getRoomId(), request, lastMsgId);

        if (cursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(request.getRoomId(), cursorPage.getList(), receiveUid), cursorPage.getTotal());
    }

    private Long getLastMsgId(Long roomId, Long receiveUid) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        if (room.isHotRoom()) {
            return null;
        }
        AssertUtil.isNotEmpty(receiveUid, "请先登录");
        Contact contact = contactDao.get(receiveUid, roomId);
        return contact.getLastMsgId();
    }

    @Override
    public ChatMemberStatisticResp getMemberStatistic() {
        Long onlineNum = userCache.getOnlineNum();
//        Long offlineNum = userCache.getOfflineNum();不展示总人数
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        resp.setOnlineNum(onlineNum);
//        resp.setTotalNum(onlineNum + offlineNum);
        return resp;
    }

    @Override
    @RedissonLock(key = "#uid")
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
        AbstractMsgMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(request.getMarkType());
        switch (MessageMarkActTypeEnum.of(request.getActType())) {
            case MARK:
                strategy.mark(uid, request.getMsgId());
                break;
            case UN_MARK:
                strategy.unMark(uid, request.getMsgId());
                break;
        }
    }

    @Override
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        Message message = messageDao.getById(request.getMsgId());
        //校验能不能执行撤回
        checkRecall(uid, message);
        //执行消息撤回
        recallMsgHandler.recall(uid, message);
    }

    @Override
    @Cacheable(cacheNames = "member", key = "'memberList.'+#req.roomId")
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq req) {
        if (Objects.equals(1L, req.getRoomId())) {
            // 大群聊可看见所有人
            return userDao.getMemberList()
                    .stream()
                    .map(a -> {
                        ChatMemberListResp resp = new ChatMemberListResp();
                        BeanUtils.copyProperties(a, resp);
                        resp.setUid(a.getId());
                        return resp;
                    }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request) {
        List<Message> messages = messageDao.listByIds(request.getMsgIds());
        messages.forEach(message -> AssertUtil.equal(uid, message.getFromUid(), "只能查询自己发送的消息"));
        return contactService.getMsgReadInfo(messages).values();
    }

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> getReadPage(@Nullable Long uid, ChatMessageReadReq request) {
        Message message = messageDao.getById(request.getMsgId());
        AssertUtil.isNotEmpty(message, "消息id有误");
        AssertUtil.equal(uid, message.getFromUid(), "只能查看自己的消息");
        CursorPageBaseResp<Contact> page;
        if (request.getSearchType() == 1) {
            //已读
            page = contactDao.getReadPage(message, request);
        } else {
            page = contactDao.getUnReadPage(message, request);
        }
        if (CollectionUtil.isEmpty(page.getList())) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(page, RoomAdapter.buildReadResp(page.getList()), 0L);
    }

    @Async(HULA_EXECUTOR)
    @Override
    //@RedissonLock(key = "#uid")
    public void msgRead(Long uid, ChatMessageMemberReq request) {
        Contact contact = contactDao.get(uid, request.getRoomId());
        if (Objects.nonNull(contact)) {
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactDao.updateById(update);
        } else {
            log.error("uid --> ", uid, "roomId --> ", request.getRoomId());
//            contactDao.save(uid, request.getRoomId());
        }
    }

    @Override
    public List<Message> getMsgByIds(List<Long> msgIds) {
        return messageDao.listByIds(msgIds);
    }

    private void checkRecall(Long uid, Message message) {
        AssertUtil.isNotEmpty(message, "消息有误");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(), "消息无法撤回");
        boolean isChatManager = roleService.hasRole(uid, RoleTypeEnum.CHAT_MANAGER);
        if (isChatManager) {
            return;
        }
        boolean self = Objects.equals(uid, message.getFromUid());
        AssertUtil.isTrue(self, "抱歉,您没有权限");
        long between = DateUtil.between(message.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between < 2, "超过2分钟的消息不能撤回");
    }

    public List<ChatMessageResp> getMsgRespBatch(Long roomId, List<Message> messages, Long receiveUid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        // 查询消息标志
        List<MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        List<ChatMessageResp> chatMessageResp = MessageAdapter.buildMsgResp(messages, msgMark, receiveUid);

        Room room = roomDao.getById(roomId);
        // 如果是群聊，设置消息的发送人的名称显示
        if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())) {
            Set<Long> uidSet = messages.stream().map(Message::getFromUid).collect(Collectors.toSet());
            RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
            Map<Long, GroupMember> groupMemberMap = groupMemberDao.getMemberBatch(roomGroup.getId(), uidSet).stream().collect(Collectors.toMap(GroupMember::getUid, Function.identity()));
            Map<Long, User> userMap = userDao.getByIds(uidSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));
            Map<Long, String> userFriendMapByFriendUid = userFriendDao.getByFriends(receiveUid, uidSet)
                    .stream()
                    .filter(item -> item.getDeleteStatus() == 0 && StringUtils.isNotEmpty(item.getRemark()))
                    .collect(Collectors.toMap(UserFriend::getFriendUid, UserFriend::getRemark));

            // 设置 user 名称
            chatMessageResp.forEach(item -> {
                ChatMessageResp.UserInfo fromUser = item.getFromUser();
                long uid = Long.parseLong(fromUser.getUid());
                GroupMember groupMember = groupMemberMap.get(uid);
                String friendRemark = userFriendMapByFriendUid.get(uid);

                // 优先显示好友备注
                if (StringUtils.isNotEmpty(friendRemark)){
                    fromUser.setNickname(friendRemark);
                }
                else if (groupMember != null){
                    String myName = groupMember.getMyName();
                    if (StringUtils.isNotEmpty(myName)){
                        fromUser.setNickname(myName);
                    }else {
                        User user = userMap.get(uid);
                        fromUser.setNickname(user.getName());
                    }
                }
            });
        }

        return chatMessageResp;
    }

}
