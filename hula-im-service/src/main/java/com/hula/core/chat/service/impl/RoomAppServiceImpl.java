package com.hula.core.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.common.annotation.RedissonLock;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.common.event.GroupMemberAddEvent;
import com.hula.core.chat.dao.ContactDao;
import com.hula.core.chat.dao.GroupMemberDao;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.dto.RoomBaseInfo;
import com.hula.core.chat.domain.entity.*;
import com.hula.core.chat.domain.enums.GroupRoleAPPEnum;
import com.hula.core.chat.domain.enums.GroupRoleEnum;
import com.hula.core.chat.domain.enums.HotFlagEnum;
import com.hula.core.chat.domain.enums.RoomTypeEnum;
import com.hula.core.chat.domain.vo.request.ChatMessageMemberReq;
import com.hula.core.chat.domain.vo.request.ChatMessageReq;
import com.hula.core.chat.domain.vo.request.ContactFriendReq;
import com.hula.core.chat.domain.vo.request.GroupAddReq;
import com.hula.core.chat.domain.vo.request.RoomApplyReq;
import com.hula.core.chat.domain.vo.request.RoomInfoReq;
import com.hula.core.chat.domain.vo.request.RoomMyInfoReq;
import com.hula.core.chat.domain.vo.request.contact.ContactHideReq;
import com.hula.core.chat.domain.vo.request.contact.ContactNotificationReq;
import com.hula.core.chat.domain.vo.request.contact.ContactShieldReq;
import com.hula.core.chat.domain.vo.request.contact.ContactTopReq;
import com.hula.core.chat.domain.vo.request.member.MemberAddReq;
import com.hula.core.chat.domain.vo.request.member.MemberDelReq;
import com.hula.core.chat.domain.vo.request.member.MemberReq;
import com.hula.core.chat.domain.vo.request.room.AnnouncementsParam;
import com.hula.core.chat.domain.vo.request.room.ReadAnnouncementsParam;
import com.hula.core.chat.domain.vo.request.room.RoomGroupReq;
import com.hula.core.chat.domain.vo.response.AnnouncementsResp;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.chat.domain.vo.response.ChatRoomResp;
import com.hula.core.chat.domain.vo.response.MemberResp;
import com.hula.core.chat.domain.vo.response.ReadAnnouncementsResp;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.RoomAppService;
import com.hula.core.chat.service.RoomService;
import com.hula.core.chat.service.adapter.ChatAdapter;
import com.hula.core.chat.service.adapter.MemberAdapter;
import com.hula.core.chat.service.adapter.MessageAdapter;
import com.hula.core.chat.service.adapter.RoomAdapter;
import com.hula.core.chat.service.cache.*;
import com.hula.core.chat.service.strategy.msg.AbstractMsgHandler;
import com.hula.core.chat.service.strategy.msg.MsgHandlerFactory;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.RoleTypeEnum;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.resp.ws.ChatMemberResp;
import com.hula.core.user.domain.vo.resp.ws.WSMemberChange;
import com.hula.core.user.service.FriendService;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.adapter.WsAdapter;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.user.service.cache.UserInfoCache;
import com.hula.core.user.service.impl.PushService;
import com.hula.enums.GroupErrorEnum;
import com.hula.utils.AssertUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class RoomAppServiceImpl implements RoomAppService {
    
    private ContactDao contactDao;
    private RoomCache roomCache;
    private RoomGroupCache roomGroupCache;
    private RoomFriendCache roomFriendCache;
    private UserInfoCache userInfoCache;
    private MessageDao messageDao;
    private HotRoomCache hotRoomCache;
    private UserCache userCache;
	private RoomAnnouncementsCache roomAnnouncementsCache;
    private GroupMemberDao groupMemberDao;
    private UserDao userDao;
    private ChatService chatService;
    private RoleService roleService;
    private ApplicationEventPublisher applicationEventPublisher;
    private RoomService roomService;
    private GroupMemberCache groupMemberCache;
    private PushService pushService;
	private FriendService friendService;
    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid) {
        // 查出用户要展示的会话列表
        CursorPageBaseResp<Long> page;
		HashMap<String, Contact> contactHashMap = new HashMap<>();
        if (Objects.nonNull(uid)) {
            Double hotEnd = getCursorOrNull(request.getCursor());
            Double hotStart = null;
            // 用户基础会话
            CursorPageBaseResp<Contact> contactPage = contactDao.getContactPage(uid, request);
			contactHashMap = contactPage.getList().stream().collect(Collectors.toMap(
					contact -> StrUtil.format("{}_{}", contact.getUid(), contact.getRoomId()),
					contact -> contact, (existing, replacement) -> existing, HashMap::new
			));
            List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
            if (!contactPage.getIsLast()) {
                hotStart = getCursorOrNull(contactPage.getCursor());
            }
            // 热门房间
            Set<ZSetOperations.TypedTuple<String>> typedTuples = hotRoomCache.getRoomRange(hotStart, hotEnd);
            List<Long> hotRoomIds = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).map(Long::parseLong).collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);
            // 基础会话和热门房间合并
            page = CursorPageBaseResp.init(contactPage, baseRoomIds, 0L);
        } else {// 用户未登录，只查全局房间
            CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(request);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds,0L);
        }
        // 最后组装会话信息（名称，头像，未读数等）
        List<ChatRoomResp> result = buildContactResp(contactHashMap, uid, page.getList());
        return CursorPageBaseResp.init(page, result,0L);
    }

    @Override
    public ChatRoomResp getContactDetail(Long uid, Long roomId) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        return buildContactResp(new HashMap<>(), uid, Collections.singletonList(roomId)).getFirst();
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, @Valid ContactFriendReq req) {
        //处理群聊
        if (Objects.equals(req.getRoomType(), RoomTypeEnum.GROUP.getType())) {
            roomService.checkUser(uid, req.getId());
            return buildContactResp(new HashMap<>(), uid, Collections.singletonList(req.getId())).getFirst();
        }
        RoomFriend friendRoom = roomService.getFriendRoom(uid, req.getId());
        AssertUtil.isNotEmpty(friendRoom, "他不是您的好友");
        return buildContactResp(new HashMap<>(), uid, Collections.singletonList(friendRoom.getRoomId())).getFirst();
    }

    @Override
    public IPage<GroupListVO> groupList(Long uid, IPage<GroupListVO>  page) {
        roomService.groupList(uid,page);
        return page;
    }

	/**
	 * 申请加群
	 *
	 * @param req
	 */
	@Override
	@RedissonLock(key = "#uid")
	public Boolean applyGroup(Long uid, RoomApplyReq req){
		Long targetGroupId = req.getTargetGroupId();
		// 获取到申请加入的群聊
		RoomGroup roomGroup = roomGroupCache.get(targetGroupId);
		if(ObjectUtil.isNull(roomGroup)){
			return false;
		}

		// 获取到这个群的管理员的人的信息
		List<Long> groupAdminIds = roomService.getGroupUsers(roomGroup.getId(), true);
		User userInfo = userCache.getUserInfo(uid);
		String msg = StrUtil.format("用户{}申请加入群聊{}", userInfo.getName(), roomGroup.getName());

		for (Long groupAdminId : groupAdminIds) {
			friendService.createUserApply(uid, groupAdminId, msg, 2);
		}

		// 给群里的管理员发送申请
		pushService.sendPushMsg(MessageAdapter.buildRoomGroupMessage(msg), groupAdminIds, uid);
		return true;
	}

	/**
	 * 校验用户在群里的权限
	 * @param uid
	 * @return
	 */
	public GroupMember verifyGroupPermissions(Long uid, RoomGroup roomGroup) {
		if(ObjectUtil.isNull(roomGroup)){
			throw new RuntimeException("群聊不存在!");
		}

		GroupMember groupMember = groupMemberDao.getMember(roomGroup.getId(), uid);
		if(ObjectUtil.isNull(groupMember)){
			throw new RuntimeException(StrUtil.format("您不是{}的群成员!", roomGroup.getName()));
		}
		return groupMember;
	}

	/**
	 * 群主，管理员才可以修改
	 * @param uid
	 * @param request
	 * @return
	 */
	@Override
	public Boolean updateRoomInfo(Long uid, RoomInfoReq request) {
		// 1.校验修改权限
		RoomGroup roomGroup = roomGroupCache.get(request.getId());
		GroupMember groupMember = verifyGroupPermissions(uid, roomGroup);

		if(GroupRoleEnum.MEMBER.getType().equals(groupMember.getRole())) {
			return false;
		}

		// 2.修改群信息
		roomGroup.setAvatar(request.getAvatar());
		roomGroup.setName(request.getName());
		Boolean success = roomService.updateRoomInfo(roomGroup);

		// 3.通知群里所有人群信息修改了
		if(success){
			roomGroupCache.delete(roomGroup.getId());
			roomGroupCache.evictGroup(roomGroup.getAccount());
			List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
			pushService.sendPushMsg(RoomAdapter.buildRoomGroupChangeWS(roomGroup.getRoomId(), roomGroup.getName(), roomGroup.getAvatar()), memberUidList, uid);
		}
		return success;
	}

	@Override
	public Boolean updateMyRoomInfo(Long uid, RoomMyInfoReq request) {
		// 1.校验修改权限
		RoomGroup roomGroup = roomGroupCache.get(request.getId());
		GroupMember member = verifyGroupPermissions(uid, roomGroup);

		// 2.修改我的信息
		boolean equals = member.getMyName().equals(StrUtil.isEmpty(request.getMyName())? "": request.getMyName());

		member.setRemark(request.getRemark());
		member.setMyName(request.getMyName());
		boolean success = groupMemberDao.updateById(member);

		// 3.通知群里所有人我的信息改变了
		if(!equals && success){
			List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
			pushService.sendPushMsg(RoomAdapter.buildMyRoomGroupChangeWS(roomGroup.getRoomId(), uid, request.getMyName()), memberUidList, uid);
		}
		return success;
	}

	@Override
	public Boolean setTop(Long uid, ContactTopReq request) {
		// 1.判断会话我有没有
		Contact contact = contactDao.get(uid, request.getRoomId());
		if(ObjectUtil.isNull(contact)){
			return false;
		}

		// 2.置顶
		contact.setTop(request.getTop());
		return contactDao.updateById(contact);
	}

	@Override
	@Transactional
	public Boolean pushAnnouncement(Long uid, AnnouncementsParam param) {
		RoomGroup roomGroup = roomGroupCache.get(param.getRoomId());
		List<Long> uids = roomService.getGroupUsers(roomGroup.getId(), false);
		if(CollUtil.isNotEmpty(uids)){
			LocalDateTime now = LocalDateTime.now();
			Announcements announcements = new Announcements();
			announcements.setContent(param.getContent());
			announcements.setRoomId(param.getRoomId());
			announcements.setUid(uid);
			announcements.setTop(param.getTop());
			announcements.setPublishTime(now);
			roomService.saveAnnouncements(announcements);

			// 创建已读的信息
			List<AnnouncementsReadRecord> announcementsReadRecordList = new ArrayList<>();
			uids.forEach(item -> {
				AnnouncementsReadRecord readRecord = new AnnouncementsReadRecord();
				readRecord.setAnnouncementsId(announcements.getId());
				readRecord.setUid(item);
				readRecord.setIsCheck(false);
				announcementsReadRecordList.add(readRecord);
			});
			// 批量添加未读消息
			Boolean saved = roomService.saveBatchAnnouncementsRecord(announcementsReadRecordList);
			if(saved){
				// 发送公告消息、推送群成员公告内容
				chatService.sendMsg(MessageAdapter.buildAnnouncementsMsg(param.getRoomId(), announcements), uid);
				pushService.sendPushMsg(MessageAdapter.buildRoomGroupAnnouncement(announcements), uids, uid);
			}
			return saved;
		}
		return false;
	}

	@Override
	public Boolean announcementEdit(Long uid, AnnouncementsParam param) {
		RoomGroup roomGroup = roomGroupCache.get(param.getRoomId());
		List<Long> uids = roomService.getGroupUsers(roomGroup.getId(), false);
		if(CollUtil.isNotEmpty(uids)){
			AnnouncementsResp announcement = roomService.getAnnouncement(param.getId());
			if(ObjectUtil.isNull(announcement)){
				return false;
			}
			Announcements announcements = new Announcements();
			announcements.setId(announcement.getId());
			announcements.setContent(param.getContent());
			announcements.setTop(param.getTop());
			Boolean edit = roomService.updateAnnouncement(announcements);
			if(edit){
				chatService.sendMsg(MessageAdapter.buildAnnouncementsMsg(param.getRoomId(), announcements), uid);
				pushService.sendPushMsg(MessageAdapter.buildEditRoomGroupAnnouncement(announcements), uids, uid);
			}
			return edit;
		}
		return false;
	}

	@Override
	public IPage<Announcements> announcementList(Long roomId, IPage<Announcements> page) {
		return roomService.announcementList(roomId, page);
	}

	@Override
	public Boolean readAnnouncement(Long uid, ReadAnnouncementsParam param) {
		// 1.更新已读状态
		Boolean success = roomService.readAnnouncement(uid, param.getAnnouncementId());

		if(success){
			// 2.刷新最新的已读数量，通知所有人有人对 announcementId 已读了
			roomAnnouncementsCache.add(param.getAnnouncementId(), uid);

			List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(param.getRoomId());
			pushService.sendPushMsg(MessageAdapter.buildReadRoomGroupAnnouncement(new ReadAnnouncementsResp(uid, roomAnnouncementsCache.get(param.getAnnouncementId()))), memberUidList, uid);
		}
		return success;
	}

	@Override
	public AnnouncementsResp getAnnouncement(Long uid, ReadAnnouncementsParam param) {
		// 1.鉴权
		roomService.checkUser(uid, param.getRoomId());

		// 2.获取公告
		AnnouncementsResp announcement = roomService.getAnnouncement(param.getAnnouncementId());

		// 3.查询公告已读数量
		Long count = roomAnnouncementsCache.get(param.getAnnouncementId());
		if(count < 1){
			count = roomService.getAnnouncementReadCount(param.getAnnouncementId());
			roomAnnouncementsCache.load(Arrays.asList(param.getAnnouncementId()));
		}

		// todo 需要测试看看重新加载公告已读数量对不对
		announcement.setCount(count);
		return announcement;
	}

	@Override
	public Boolean announcementDelete(Long uid, Long id) {
		// 1. 鉴权
		AnnouncementsResp resp = roomService.getAnnouncement(id);

		RoomGroup roomGroup = roomGroupCache.get(resp.getRoomId());
		GroupMember groupMember = verifyGroupPermissions(uid, roomGroup);

		if(GroupRoleEnum.MEMBER.getType().equals(groupMember.getRole())) {
			return false;
		}

		return roomService.announcementDelete(id);
	}

	@Override
	public Boolean setHide(Long uid, ContactHideReq req) {
		return contactDao.setHide(uid, req.getRoomId(), req.getHide());
	}

	@Override
	public Boolean setNotification(Long uid, ContactNotificationReq request) {
		// 1. 判断会话我有没有
		Contact contact = contactDao.get(uid, request.getRoomId());
		if(ObjectUtil.isNull(contact)){
			return false;
		}

		// 2. 修改会话通知类型并通知其他终端
		contact.setMuteNotification(request.getType());

		// 3.通知所有设备我已经开启/关闭这个房间的免打扰
		pushService.sendPushMsg(WsAdapter.buildContactNotification(request) , uid, uid);
		return contactDao.updateById(contact);
	}

	@Override
	public Boolean setShield(Long uid, ContactShieldReq request) {
		Contact contact = contactDao.get(uid, request.getRoomId());
		if(ObjectUtil.isNull(contact)){
			return false;
		}

		String name;
		Room room = roomCache.get(request.getRoomId());
		if(room.getType().equals(RoomTypeEnum.GROUP.getType())){
			// 1. 把群成员的信息设置为禁止
			RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
			name = roomGroup.getName();
			groupMemberDao.setMemberDeFriend(roomGroup.getId(), uid, request.getState());
		} else {
			// 2. 把两个人的房间全部设置为禁止
			RoomFriend roomFriend = roomFriendCache.get(request.getRoomId());
			roomService.updateState(uid.equals(roomFriend.getUid1()), roomFriend.getUid1(), roomFriend.getUid2(), request.getState());

			name = userCache.getUserInfo(roomFriend.getUid1().equals(uid) ? roomFriend.getUid2() : roomFriend.getUid1()).getName();
		}

		// 3. 通知所有设备我已经屏蔽这个房间
		pushService.sendPushMsg(WsAdapter.buildShieldContact(request.getState(), name), uid, uid);
		roomFriendCache.delete(request.getRoomId());
		contact.setShield(request.getState());
		return contactDao.updateById(contact);
	}

	@Override
	public List<RoomGroup> searchGroup(RoomGroupReq req) {
		return roomGroupCache.searchGroup(req.getAccount());
	}

	@Override
    public MemberResp getGroupDetail(Long uid, long roomId) {
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(roomGroup, "roomId有误");
        Long onlineNum;
        if (isHotGroup(room)) {
            // 热点群从redis取人数
            onlineNum = userCache.getOnlineNum();
        } else {
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId(), null);
            onlineNum = userDao.getOnlineCount(memberUidList).longValue();
        }

		// 获取群成员数、备注、我的群名称
		Integer memberNum = groupMemberCache.getMemberUidList(roomId).size();
		GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);

        GroupRoleAPPEnum groupRole = getGroupRole(uid, roomGroup, room);
        return MemberResp.builder()
                .avatar(roomGroup.getAvatar())
                .roomId(roomId)
                .groupName(roomGroup.getName())
                .onlineNum(onlineNum)
				.memberNum(memberNum)
				.account(roomGroup.getAccount())
				.remark(member.getRemark())
				.myName(member.getMyName())
                .role(groupRole.getType())
                .build();
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        List<Long> memberUidList;
        if (isHotGroup(room)) {
            // 全员群展示所有用户
            memberUidList = null;
        } else {// 只展示房间内的群成员
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId(), null);
        }
        return chatService.getMemberPage(memberUidList, request);
    }

    @Override
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        if (isHotGroup(room)) {
            // 全员群展示所有用户100名
            List<User> memberList = userDao.getMemberList();
            return MemberAdapter.buildMemberList(memberList);
        } else {
            RoomGroup roomGroup = roomGroupCache.get(room.getId());
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId(), null);
            Map<Long, User> batch = userInfoCache.getBatch(memberUidList);
            return MemberAdapter.buildMemberList(batch);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMember(Long uid, MemberDelReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, "房间号有误");
        GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
        AssertUtil.isNotEmpty(self, GroupErrorEnum.USER_NOT_IN_GROUP, "groupMember");
        // 1. 判断被移除的人是否是群主或者管理员  （群主不可以被移除，管理员只能被群主移除）
        Long removedUid = request.getUid();
        // 1.1 群主 非法操作
        AssertUtil.isFalse(groupMemberDao.isLord(roomGroup.getId(), removedUid), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE, "");
        // 1.2 管理员 判断是否是群主操作
        if (groupMemberDao.isManager(roomGroup.getId(), removedUid)) {
            Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
            AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        }
        // 1.3 普通成员 判断是否有权限操作
        AssertUtil.isTrue(hasPower(self), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        GroupMember member = groupMemberDao.getMember(roomGroup.getId(), removedUid);
        AssertUtil.isNotEmpty(member, "用户已经移除");
        groupMemberDao.removeById(member.getId());
        // 发送移除事件告知群成员
        List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
		if(!memberUidList.contains(request.getUid())){
			memberUidList.add(request.getUid());
		}
        WsBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), member.getUid());
        pushService.sendPushMsg(ws, memberUidList, uid);
        groupMemberCache.evictMemberUidList(room.getId());
		groupMemberCache.evictMemberDetail(room.getId(), removedUid);
    }


    @Override
    @RedissonLock(key = "#request.roomId")
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long uid, MemberAddReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        AssertUtil.isFalse(isHotGroup(room), "全员群无需邀请好友");
        RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, "房间号有误");
        GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
        AssertUtil.isNotEmpty(self, "您不是群成员");
        List<Long> memberBatch = groupMemberDao.getMemberBatch(roomGroup.getId(), request.getUidList());
        Set<Long> existUid = new HashSet<>(memberBatch);
        List<Long> waitAddUidList = request.getUidList().stream().filter(a -> !existUid.contains(a)).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(waitAddUidList)) {
            return;
        }
        List<GroupMember> groupMembers = MemberAdapter.buildMemberAdd(roomGroup.getId(), waitAddUidList);
        groupMemberDao.saveBatch(groupMembers);

        User user = userInfoCache.get(uid);
        List<Long> uidList = groupMembers.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, userInfoCache.getBatch(uidList));
        chatService.sendMsg(chatMessageReq, User.UID_SYSTEM);

        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));
    }

    @Override
    public Long addGroup(Long uid, GroupAddReq request) {
        List<Long> userIdList = request.getUidList();
        AssertUtil.isTrue(userIdList.size() > 1,"群聊人数应大于2人");
		RoomGroup roomGroup = roomService.createGroupRoom(uid, request);

		// 批量保存群成员
		List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(userIdList, roomGroup.getId());
		groupMemberDao.saveBatch(groupMembers);

        // 发送邀请加群消息 ==> 触发每个人的会话
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));
		return roomGroup.getRoomId();
    }

    private boolean hasPower(GroupMember self) {
        return Objects.equals(self.getRole(), GroupRoleEnum.LEADER.getType())
                || Objects.equals(self.getRole(), GroupRoleEnum.MANAGER.getType())
                || roleService.hasRole(self.getUid(), RoleTypeEnum.ADMIN);
    }

    private GroupRoleAPPEnum getGroupRole(Long uid, RoomGroup roomGroup, Room room) {
        GroupMember member = Objects.isNull(uid) ? null : groupMemberDao.getMember(roomGroup.getId(), uid);
        if (Objects.nonNull(member)) {
            return GroupRoleAPPEnum.of(member.getRole());
        } else if (isHotGroup(room)) {
            return GroupRoleAPPEnum.MEMBER;
        } else {
            return GroupRoleAPPEnum.REMOVE;
        }
    }

    private boolean isHotGroup(Room room) {
        return HotFlagEnum.YES.getType().equals(room.getHotFlag());
    }

    private List<Contact> buildContact(List<Pair<Long, Double>> list, Long uid) {
        List<Long> roomIds = list.stream().map(Pair::getKey).collect(Collectors.toList());
        Map<Long, Room> batch = roomCache.getBatch(roomIds);
        Map<Long, Contact> contactMap = new HashMap<>();
        if (Objects.nonNull(uid)) {
            List<Contact> byRoomIds = contactDao.getByRoomIds(roomIds, uid);
            contactMap = byRoomIds.stream().collect(Collectors.toMap(Contact::getRoomId, Function.identity()));
        }
        Map<Long, Contact> finalContactMap = contactMap;
        return list.stream().map(pair -> {
            Long roomId = pair.getKey();
            Contact contact = finalContactMap.get(roomId);
            if (Objects.isNull(contact)) {
                contact = new Contact();
                contact.setRoomId(pair.getKey());
                Room room = batch.get(roomId);
                contact.setLastMsgId(room.getLastMsgId());
            }
            contact.setActiveTime(new Date(pair.getValue().longValue()));
            return contact;
        }).collect(Collectors.toList());
    }

    private Double getCursorOrNull(String cursor) {
        if (StringUtils.isEmpty(cursor)) {
            return null;
        }
        return Optional.of(cursor).map(Double::parseDouble).orElse(null);
    }

	/**
	 * @param contactMap 会话映射
	 * @param uid 当前登录的用户
	 * @param roomIds 所有会话对应的房间
	 * @return
	 */
    @NotNull
    private List<ChatRoomResp> buildContactResp(HashMap<String, Contact> contactMap, Long uid, List<Long> roomIds) {
        // 表情和头像
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIds, uid);
        // 最后一条消息
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        List<Message> messages = CollectionUtil.isEmpty(msgIds) ? new ArrayList<>() : messageDao.listByIds(msgIds);
        Map<Long, Message> msgMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        Map<Long, User> lastMsgUidMap = userInfoCache.getBatch(messages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        // 消息未读数
        Map<Long, Integer> unReadCountMap = getUnReadCountMap(uid, roomIds);
        return roomBaseInfoMap.values().stream().map(room -> {
                    ChatRoomResp resp = new ChatRoomResp();
                    Long roomId = room.getRoomId();
                    RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(roomId);
					Contact contact = contactMap.get(StrUtil.format("{}_{}", uid, roomId));
					if(ObjectUtil.isNotNull(contact)){
						resp.setHide(contact.getHide());
						resp.setShield(contact.getShield());
						resp.setMuteNotification(contact.getMuteNotification());
						resp.setTop(contact.getTop());
					} else {
						resp.setHide(true);
						resp.setShield(true);
						resp.setMuteNotification(2);
						resp.setTop(false);
					}
					resp.setId(room.getId());
                    resp.setAvatar(roomBaseInfo.getAvatar());
                    resp.setRoomId(roomId);
					resp.setAccount(room.getAccount());
                    resp.setActiveTime(room.getActiveTime());
                    resp.setHotFlag(roomBaseInfo.getHotFlag());
                    resp.setType(roomBaseInfo.getType());
                    resp.setName(roomBaseInfo.getName());
					resp.setOperate(roomBaseInfo.getRole());
					resp.setRemark(roomBaseInfo.getRemark());
					resp.setMyName(roomBaseInfo.getMyName());
                    Message message = msgMap.get(room.getLastMsgId());
                    if (Objects.nonNull(message)) {
                        AbstractMsgHandler strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
                        // 判断是群聊还是单聊
                        if (Objects.equals(roomBaseInfo.getType(), RoomTypeEnum.GROUP.getType())) {
							GroupMember messageUser = groupMemberCache.getMemberDetail(roomBaseInfo.getRoomId(), message.getFromUid());
							resp.setText(ObjectUtil.isNotNull(messageUser) && StrUtil.isNotEmpty(messageUser.getMyName())? messageUser.getMyName(): (lastMsgUidMap.get(message.getFromUid()).getName()) + ":" + strategyNoNull.showContactMsg(message));
                        } else {
                            resp.setText(strategyNoNull.showContactMsg(message));
                        }
                    }
                    resp.setUnreadCount(unReadCountMap.getOrDefault(roomId, 0));
                    return resp;
                }).sorted(Comparator.comparing(ChatRoomResp::getActiveTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取未读数
     */
    private Map<Long, Integer> getUnReadCountMap(Long uid, List<Long> roomIds) {
        if (Objects.isNull(uid)) {
            return new HashMap<>();
        }
        List<Contact> contacts = contactDao.getByRoomIds(roomIds, uid);
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(), messageDao.getUnReadCount(contact.getRoomId(), contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private Map<Long, User> getFriendRoomMap(List<Long> roomIds, Long uid) {
        if (CollectionUtil.isEmpty(roomIds)) {
            return new HashMap<>();
        }
        Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(roomIds);
        Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(), uid);
        Map<Long, User> userBatch = userInfoCache.getBatch(new ArrayList<>(friendUidSet));
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> {
                    Long friendUid = ChatAdapter.getFriendUid(roomFriend, uid);
                    return userBatch.get(friendUid);
                }));
    }

    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        // 房间根据好友和群组类型分组
        Map<Integer, List<Long>> groupRoomIdMap = roomMap.values().stream().filter(Objects::nonNull).collect(Collectors.groupingBy(Room::getType, Collectors.mapping(Room::getId, Collectors.toList())));
        // 获取群组信息
        List<Long> groupRoomId = groupRoomIdMap.get(RoomTypeEnum.GROUP.getType());
        Map<Long, RoomGroup> roomInfoBatch = roomGroupCache.getBatch(groupRoomId);
        // 获取好友信息
        List<Long> friendRoomId = groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRoomId, uid);

        return roomMap.values().stream().filter(Objects::nonNull).map(room -> {
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            roomBaseInfo.setRoomId(room.getId());
            roomBaseInfo.setType(room.getType());
            roomBaseInfo.setHotFlag(room.getHotFlag());
            roomBaseInfo.setLastMsgId(room.getLastMsgId());
            roomBaseInfo.setActiveTime(room.getActiveTime());
            if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                RoomGroup roomGroup = roomInfoBatch.get(room.getId());
				roomBaseInfo.setId(roomGroup.getId());
                roomBaseInfo.setName(roomGroup.getName());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
				roomBaseInfo.setAccount(roomGroup.getAccount());
				GroupMember member = groupMemberCache.getMemberDetail(roomBaseInfo.getId(), uid);
				// todo 稳定了这里可以不用判空，理论上100% 在群里
				if(ObjectUtil.isNotNull(member)){
					roomBaseInfo.setMyName(member.getMyName());
					roomBaseInfo.setRemark(member.getRemark());
					roomBaseInfo.setRole(member.getRole());
				}else {
					roomBaseInfo.setMyName("会话异常");
					roomBaseInfo.setRemark("会话异常");
					roomBaseInfo.setRole(0);
				}
            } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
                User user = friendRoomMap.get(room.getId());
				roomBaseInfo.setId(user.getId());
				roomBaseInfo.setRole(0);
                roomBaseInfo.setName(user.getName());
                roomBaseInfo.setAvatar(user.getAvatar());
				roomBaseInfo.setAccount(user.getAccount());
            }
            return roomBaseInfo;
        }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }
}
