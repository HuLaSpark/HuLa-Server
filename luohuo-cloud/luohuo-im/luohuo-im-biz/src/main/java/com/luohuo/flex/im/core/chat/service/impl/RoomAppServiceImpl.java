package com.luohuo.flex.im.core.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.im.api.PresenceApi;
import com.luohuo.flex.im.core.chat.dao.RoomFriendDao;
import com.luohuo.flex.im.core.chat.dao.RoomGroupDao;
import com.luohuo.flex.im.core.user.dao.NoticeDao;
import com.luohuo.flex.im.core.user.dao.UserApplyDao;
import com.luohuo.flex.im.core.user.dao.UserBackpackDao;
import com.luohuo.flex.im.core.user.dao.UserFriendDao;
import com.luohuo.flex.im.core.user.dao.UserPrivacyDao;
import com.luohuo.flex.im.core.user.service.NoticeService;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.*;
import com.luohuo.flex.im.domain.enums.*;
import com.luohuo.flex.im.domain.vo.request.ChatMessageReq;
import com.luohuo.flex.im.domain.vo.request.admin.AdminAddReq;
import com.luohuo.flex.im.domain.vo.request.admin.AdminRevokeReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberExitReq;
import com.luohuo.flex.im.domain.entity.msg.TextMsgReq;
import com.luohuo.flex.model.enums.ChatActiveStatusEnum;
import com.luohuo.flex.im.domain.vo.request.contact.ContactAddReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.code.GroupErrorEnum;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.event.GroupMemberAddEvent;
import com.luohuo.flex.im.core.chat.dao.ContactDao;
import com.luohuo.flex.im.core.chat.dao.GroupMemberDao;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.dto.RoomBaseInfo;
import com.luohuo.flex.im.domain.vo.request.ChatMessageMemberReq;
import com.luohuo.flex.im.domain.vo.request.ContactFriendReq;
import com.luohuo.flex.im.domain.vo.request.GroupAddReq;
import com.luohuo.flex.im.domain.vo.request.RoomInfoReq;
import com.luohuo.flex.im.domain.vo.request.RoomMyInfoReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactHideReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactNotificationReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactShieldReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactTopReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberAddReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberDelReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberReq;
import com.luohuo.flex.im.domain.vo.request.room.AnnouncementsParam;
import com.luohuo.flex.im.domain.vo.request.room.ReadAnnouncementsParam;
import com.luohuo.flex.im.domain.vo.request.room.RoomGroupReq;
import com.luohuo.flex.im.domain.vo.response.AnnouncementsResp;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.domain.vo.response.ChatRoomResp;
import com.luohuo.flex.im.domain.vo.response.MemberResp;
import com.luohuo.flex.im.domain.vo.response.ReadAnnouncementsResp;
import com.luohuo.flex.im.core.chat.service.ChatService;
import com.luohuo.flex.im.core.chat.service.RoomAppService;
import com.luohuo.flex.im.core.chat.service.RoomService;
import com.luohuo.flex.im.core.chat.service.adapter.ChatAdapter;
import com.luohuo.flex.im.core.chat.service.adapter.MemberAdapter;
import com.luohuo.flex.im.core.chat.service.adapter.MessageAdapter;
import com.luohuo.flex.im.core.chat.service.adapter.RoomAdapter;
import com.luohuo.flex.im.core.chat.service.cache.GroupMemberCache;
import com.luohuo.flex.im.core.chat.service.cache.HotRoomCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomAnnouncementsCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomFriendCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomGroupCache;
import com.luohuo.flex.im.core.chat.service.strategy.msg.AbstractMsgHandler;
import com.luohuo.flex.im.core.chat.service.strategy.msg.MsgHandlerFactory;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.im.domain.vo.req.MergeMessageReq;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;
import com.luohuo.flex.model.entity.ws.WSMemberChange;
import com.luohuo.flex.im.core.user.service.FriendService;
import com.luohuo.flex.im.core.user.service.RoleService;
import com.luohuo.flex.im.core.user.service.adapter.WsAdapter;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.core.user.service.impl.PushService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.luohuo.flex.im.core.chat.constant.GroupConst.MAX_MANAGE_COUNT;
import static com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum.UNREAD;

@Slf4j
@Service
@AllArgsConstructor
public class RoomAppServiceImpl implements RoomAppService, InitializingBean {

	private final UserBackpackDao userBackpackDao;
	private final RoomGroupDao roomGroupDao;
	private final RoomFriendDao roomFriendDao;
	private final NoticeDao noticeDao;
	private final UserApplyDao userApplyDao;
	private ContactDao contactDao;
	private RoomCache roomCache;
	private final UserFriendDao userFriendDao;
	private UserPrivacyDao userPrivacyDao;
	private RoomGroupCache roomGroupCache;
	private RoomFriendCache roomFriendCache;
	private CachePlusOps cachePlusOps;
	private UserCache userCache;
	private MessageDao messageDao;
	private HotRoomCache hotRoomCache;
	private UserSummaryCache userSummaryCache;
	private RoomAnnouncementsCache roomAnnouncementsCache;
	private GroupMemberDao groupMemberDao;
	private UserDao userDao;
	private ChatService chatService;
	private RoleService roleService;
	private RoomService roomService;
	private UidGenerator uidGenerator;
	private NoticeService noticeService;
	private GroupMemberCache groupMemberCache;
	private PushService pushService;
	private FriendService friendService;
	private PresenceApi presenceApi;
	private TransactionTemplate transactionTemplate;

	private void warmUpGroupMemberCache(Long roomId) {
		// 1. 查询房间中所有用户
		List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
		// 2. 更新 群里与用户的关系
		CacheKey cacheKey = PresenceCacheKeyBuilder.groupMembersKey(roomId);
		memberUidList.forEach(memberId -> cachePlusOps.sAdd(cacheKey, memberId));
	}

	private void warmUpUserRoomCache(Long uid) {
		// 1. 查询房间中所有用户
		List<Long> roomIdList = groupMemberCache.getJoinedRoomIds(uid);
		// 2. 更新 群里与用户的关系
		CacheKey cacheKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
		roomIdList.forEach(roomId -> cachePlusOps.sAdd(cacheKey, roomId));
	}

	/**
	 * 这里需要分页实现，根据情况加载
	 */
	@Override
	public void afterPropertiesSet() {
		// 分页加载所有群
		roomGroupDao.list().parallelStream().map(RoomGroup::getRoomId).forEach(this::warmUpGroupMemberCache);
		List<Long> roomFriendList = roomFriendDao.list().parallelStream().map(RoomFriend::getRoomId).toList();
		if (CollUtil.isNotEmpty(roomFriendList)) {
			friendService.warmUpRoomMemberCache(roomFriendList);
		}
		// 分页加载所有用户有多少个群的缓存
		List<User> list = userDao.list();
		list.parallelStream().map(User::getId).forEach(this::warmUpUserRoomCache);
	}

	@Override
	public Boolean createContact(Long uid, ContactAddReq request) {
		// 1. 检查对方是否允许临时会话
		UserPrivacy privacy = userPrivacyDao.getByUid(request.getToUid());
		if (privacy != null && (privacy.getIsPrivate() || !privacy.getAllowTempSession())) {
			throw new BizException("对方设置了不接受临时会话");
		}

		// 2. 检查是否已存在临时会话
		UserFriend userFriend = userFriendDao.getByFriend(uid, request.getToUid());
		if (userFriend != null && userFriend.getIsTemp()) {
			throw new BizException("已存在临时会话，请勿重复创建");
		}

		// 创建一个聊天房间
		RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, request.getToUid()));
		// 创建双方好友关系
		friendService.createFriend(roomFriend.getRoomId(), uid, request.getToUid());
		// 发送一条临时会话消息
		chatService.sendMsg(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId(), true), uid);
		return true;
	}

	@Override
	public CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid) {
		// 1. 获取登录用户的会话数据
		Double hotEnd = getCursorOrNull(request.getCursor());
		Double hotStart = null;
		// 用户基础会话
		CursorPageBaseResp<Contact> contactPage = contactDao.getContactPage(uid, request);
		HashMap<String, Contact> contactHashMap = contactPage.getList().stream().collect(Collectors.toMap(
				contact -> StrUtil.format("{}_{}", contact.getUid(), contact.getRoomId()),
				contact -> contact, (existing, replacement) -> existing, HashMap::new
		));
		List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
		if (!contactPage.getIsLast()) {
			hotStart = getCursorOrNull(contactPage.getCursor());
		}
		// 热门房间
		Set<ZSetOperations.TypedTuple<Object>> typedTuples = hotRoomCache.getRoomRange(hotStart, hotEnd);
		List<Long> hotRoomIds = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).map(item -> Long.parseLong(item.toString())).collect(Collectors.toList());
		baseRoomIds.addAll(hotRoomIds);
		// 基础会话和热门房间合并
		CursorPageBaseResp<Long> page = CursorPageBaseResp.init(contactPage, baseRoomIds, 0L);

		// 2. 最后组装会话信息（名称，头像，未读数等）
		List<ChatRoomResp> result = buildContactResp(contactHashMap, uid, page.getList());
		return CursorPageBaseResp.init(page, result, 0L);
	}

	/**
	 * 返回当前登录用户的全部会话
	 *
	 * @param uid
	 * @return
	 */
	@Override
	public List<ChatRoomResp> getContactPage(Long uid) {
		// 1. 查出用户要展示的会话列表
		List<Contact> contacts = contactDao.getAllContactsByUid(uid);

		// 2. 构建会话映射表（uid_roomId -> Contact）
		HashMap<String, Contact> contactMap = contacts.stream()
				.collect(Collectors.toMap(
						contact -> StrUtil.format("{}_{}", contact.getUid(), contact.getRoomId()),
						contact -> contact,
						(existing, replacement) -> existing,
						HashMap::new
				));

		// 3. 提取所有基础会话的 roomId
		List<Long> baseRoomIds = contacts.stream().map(Contact::getRoomId).collect(Collectors.toList());

		// 5. 组装最终会话信息（名称、头像、未读数等）
		return buildContactResp(contactMap, uid, baseRoomIds);
	}

	@Override
	public ChatRoomResp getContactDetail(Long uid, Long roomId) {
		Room room = roomCache.get(roomId);
		AssertUtil.isNotEmpty(room, "房间号有误");
		return buildContactResp(contactDao.getContactMapByUid(uid), uid, Collections.singletonList(roomId)).get(0);
	}

	@Override
	public ChatRoomResp getContactDetailByFriend(Long uid, @Valid ContactFriendReq req) {
		HashMap<String, Contact> contactMap = contactDao.getContactMapByUid(uid);
		if (Objects.equals(req.getRoomType(), RoomTypeEnum.GROUP.getType())) {
			roomService.checkUser(uid, req.getId());
			return buildContactResp(contactMap, uid, Collections.singletonList(req.getId())).get(0);
		}
		RoomFriend friendRoom = roomService.getFriendRoom(uid, req.getId());
		AssertUtil.isNotEmpty(friendRoom, "他不是您的好友");
		return buildContactResp(contactMap, uid, Collections.singletonList(friendRoom.getRoomId())).get(0);
	}

	@Override
	public List<MemberResp> groupList(Long uid) {
		List<MemberResp> voList = roomService.groupList(uid);
		Set<Long> groupIdList = voList.stream().map(MemberResp::getGroupId).collect(Collectors.toSet());
		List<Long> roomIdList = voList.stream().map(MemberResp::getRoomId).collect(Collectors.toList());

		Map<Long, Long> onlineMap = presenceApi.getBatchGroupOnlineCounts(roomIdList).getData();
		List<GroupMember> members = groupMemberDao.getGroupMemberByGroupIdListAndUid(uid, groupIdList);
		Map<Long, GroupMember> map = members.stream().collect(Collectors.toMap(GroupMember::getGroupId, Function.identity()));

		// 渲染群信息
		voList.forEach(item -> {
			Long memberNum = (long) groupMemberCache.getMemberUidList(item.getRoomId()).size();
			GroupMember member = map.get(item.getGroupId());

			item.setOnlineNum(onlineMap.get(item.getRoomId()));
			item.setRoleId(getGroupRole(uid, item.getGroupId()));
			item.setMemberNum(memberNum);
			item.setRemark(member.getRemark());
			item.setMyName(member.getMyName());
		});
		return voList;
	}

	/**
	 * 增加管理员
	 *
	 * @param uid     用户ID
	 * @param request 请求信息
	 */
	@Override
	@RedissonLock(prefixKey = "addAdmin:", key = "#request.roomId")
	@Transactional(rollbackFor = Exception.class)
	public void addAdmin(Long uid, AdminAddReq request) {
		// 1. 判断群聊是否存在
		RoomGroup roomGroup = roomGroupCache.getByRoomId(request.getRoomId());
		AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

		// 2. 判断该用户是否是群主
		Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
		AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

		// 3. 判断群成员是否在群中
		Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
		AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

		// 4. 判断管理员数量是否达到上限
		// 4.1 查询现有管理员数量
		List<Long> manageUidList = groupMemberDao.getManageUidList(roomGroup.getId());
		// 4.2 去重
		HashSet<Long> manageUidSet = new HashSet<>(manageUidList);
		manageUidSet.addAll(request.getUidList());
		AssertUtil.isFalse(manageUidSet.size() > MAX_MANAGE_COUNT, GroupErrorEnum.MANAGE_COUNT_EXCEED);

		// 5. 增加管理员
		groupMemberDao.addAdmin(roomGroup.getId(), request.getUidList());

		// 每个被邀请的人都要收到邀请进群的消息
		setAdminNotice(NoticeTypeEnum.GROUP_SET_ADMIN, uid, request.getUidList(), manageUidList, roomGroup.getName());
	}

	private void setAdminNotice(NoticeTypeEnum noticeTypeEnum, Long uid, List<Long> uidList, List<Long> manageUidList, String content) {
		long uuid = uidGenerator.getUid();
		uidList.stream().filter(id -> !manageUidList.contains(id)).forEach(id -> {
			// 通知被操作的人
			noticeService.createNotice(
					RoomTypeEnum.GROUP,
					noticeTypeEnum,
					uid,
					id,
					uuid,
					id,
					content
			);

			// 通知群主
//			noticeService.createNotice(
//					RoomTypeEnum.GROUP,
//					noticeTypeEnum,
//					uid,
//					uid,
//					uuid,
//					id,
//					content
//			);
		});
	}

	/**
	 * 撤销管理员
	 *
	 * @param uid     用户ID
	 * @param request 请求信息
	 */
	@Override
	@RedissonLock(prefixKey = "revokeAdmin:", key = "#request.roomId")
	@Transactional(rollbackFor = Exception.class)
	public void revokeAdmin(Long uid, AdminRevokeReq request) {
		// 1. 判断群聊是否存在
		RoomGroup roomGroup = roomGroupCache.getByRoomId(request.getRoomId());
		AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

		// 2. 判断该用户是否是群主
		Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
		AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

		// 3. 判断群成员是否在群中
		Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
		AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

		// 4. 撤销管理员
		groupMemberDao.revokeAdmin(roomGroup.getId(), request.getUidList());
		setAdminNotice(NoticeTypeEnum.GROUP_RECALL_ADMIN, uid, request.getUidList(), new ArrayList<>(), roomGroup.getName());
	}

	/**
	 * @param roomId  房间id
	 * @param groupId 群聊id
	 * @param uid     当前人员id
	 */
	@Override
	public void createSystemFriend(Long roomId, Long groupId, Long uid) {
		// 创建会话
		chatService.createContact(uid, roomId);
		// 创建群成员
		roomService.createGroupMember(groupId, uid);
		// 创建系统消息
		friendService.createSystemFriend(uid);
		// 发送进群事件
		CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(roomId);
		CacheKey onlineGroupMembersKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomId);
		SpringUtils.publishEvent(new GroupMemberAddEvent(this, roomId, Math.toIntExact(cachePlusOps.sCard(gKey)), Math.toIntExact(cachePlusOps.sCard(onlineGroupMembersKey)), Arrays.asList(uid), uid));
	}

	/**
	 * 处理用户在线状态
	 *
	 * @param uidList 需要处理的用户
	 * @param roomId  在某个房间处理
	 * @return
	 */
//	@Async(LUOHUO_EXECUTOR)
	public void asyncOnline(List<Long> uidList, Long roomId, boolean online) {
		Set<Long> onlineList = presenceApi.getOnlineUsersList(uidList).getData();
		if (CollUtil.isEmpty(onlineList)) {
			return;
		}

		CacheKey ogmKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomId);
		for (Long uid : onlineList) {
			CacheKey ougKey = PresenceCacheKeyBuilder.onlineUserGroupsKey(uid);

			if (online) {
				// 处理在线的状态
				cachePlusOps.sAdd(ogmKey, uid);
				cachePlusOps.sAdd(ougKey, roomId);
			} else {
				// 处理离线的状态
				cachePlusOps.sRem(ougKey, roomId);
				cachePlusOps.sRem(ogmKey, uid);
			}
		}
	}

	/**
	 * 校验用户在群里的权限
	 *
	 * @param uid
	 * @return
	 */
	public GroupMember verifyGroupPermissions(Long uid, RoomGroup roomGroup) {
		if (ObjectUtil.isNull(roomGroup)) {
			throw new RuntimeException("群聊不存在!");
		}

		GroupMember groupMember = groupMemberDao.getMemberByGroupId(roomGroup.getId(), uid);
		if (ObjectUtil.isNull(groupMember)) {
			throw new RuntimeException(StrUtil.format("您不是{}的群成员!", roomGroup.getName()));
		}
		return groupMember;
	}

	/**
	 * 群主，管理员才可以修改
	 *
	 * @param uid
	 * @param request
	 * @return
	 */
	@Override
	public Boolean updateRoomInfo(Long uid, RoomInfoReq request) {
		// 1.校验修改权限
		RoomGroup roomGroup = roomGroupCache.get(request.getId());
		GroupMember groupMember = verifyGroupPermissions(uid, roomGroup);

		if (GroupRoleEnum.MEMBER.getType().equals(groupMember.getRoleId())) {
			return false;
		}

		// 2.修改群信息
		roomGroup.setAvatar(request.getAvatar());
		roomGroup.setName(request.getName());
		roomGroup.setAllowScanEnter(request.getAllowScanEnter());
		Boolean success = roomService.updateRoomInfo(roomGroup);

		// 3.通知群里所有人群信息修改了
		if (success) {
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
		boolean equals = member.getMyName().equals(StrUtil.isEmpty(request.getMyName()) ? "" : request.getMyName());
		boolean success = groupMemberDao.update(null, Wrappers.<GroupMember>lambdaUpdate()
				.set(GroupMember::getRemark, request.getRemark())
				.set(GroupMember::getMyName, request.getMyName())
				.eq(GroupMember::getId, member.getId()));

		groupMemberCache.evictMemberDetail(roomGroup.getRoomId(), uid);

		// 3.通知群里所有人我的信息改变了
		if (!equals && success) {
			List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
			pushService.sendPushMsg(RoomAdapter.buildMyRoomGroupChangeWS(roomGroup.getRoomId(), uid, request.getMyName()), memberUidList, uid);
		}
		return success;
	}

	@Override
	public Boolean setTop(Long uid, ContactTopReq request) {
		// 1.判断会话我有没有
		Contact contact = contactDao.get(uid, request.getRoomId());
		if (ObjectUtil.isNull(contact)) {
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
		if (CollUtil.isNotEmpty(uids)) {
			LocalDateTime now = LocalDateTime.now();
			Announcements announcements = new Announcements();
			announcements.setContent(param.getContent());
			announcements.setRoomId(param.getRoomId());
			announcements.setUid(uid);
			announcements.setTop(param.getTop());
			announcements.setCreateTime(now);
			announcements.setUpdateTime(now);
			roomService.saveAnnouncements(announcements);

			// 创建已读的信息
			List<AnnouncementsReadRecord> announcementsReadRecordList = new ArrayList<>();
			uids.forEach(item -> {
				AnnouncementsReadRecord readRecord = new AnnouncementsReadRecord();
				readRecord.setAnnouncementsId(announcements.getId());
				readRecord.setUid(item);
				readRecord.setIsCheck(false);
				readRecord.setCreateBy(uid);
				announcementsReadRecordList.add(readRecord);
			});
			// 批量添加未读消息
			Boolean saved = roomService.saveBatchAnnouncementsRecord(announcementsReadRecordList);
			if (saved) {
				// 发送公告消息、推送群成员公告内容
				chatService.sendMsg(MessageAdapter.buildAnnouncementsMsg(param.getRoomId(), announcements), uid);
			}
			return saved;
		}
		return false;
	}

	@Override
	public Boolean announcementEdit(Long uid, AnnouncementsParam param) {
		RoomGroup roomGroup = roomGroupCache.get(param.getRoomId());
		List<Long> uids = roomService.getGroupUsers(roomGroup.getId(), false);
		if (CollUtil.isNotEmpty(uids)) {
			AnnouncementsResp announcement = roomService.getAnnouncement(param.getId());
			if (ObjectUtil.isNull(announcement)) {
				return false;
			}
			Announcements announcements = new Announcements();
			announcements.setId(param.getId());
			announcements.setRoomId(param.getRoomId());
			announcements.setContent(param.getContent());
			announcements.setTop(param.getTop());
			announcements.setUpdateTime(TimeUtils.now());
			Boolean edit = roomService.updateAnnouncement(announcements);
			if (edit) {
				chatService.sendMsg(MessageAdapter.buildAnnouncementsMsg(param.getRoomId(), announcements), uid);
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

		if (success) {
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
		if (count < 1) {
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

		long count = userBackpackDao.countByUidAndItemId(uid, "HuLa项目贡献者专属徽章");

		if (count == 0 && GroupRoleEnum.MEMBER.getType().equals(groupMember.getRoleId())) {
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
		if (ObjectUtil.isNull(contact)) {
			return false;
		}

		// 2. 修改会话通知类型并通知其他终端
		contact.setMuteNotification(request.getType());

		// 3.通知所有设备我已经开启/关闭这个房间的免打扰
		pushService.sendPushMsg(WsAdapter.buildContactNotification(request), uid, uid);
		return contactDao.updateById(contact);
	}

	@Override
	public Boolean setShield(Long uid, ContactShieldReq request) {
		Contact contact = contactDao.get(uid, request.getRoomId());
		if (ObjectUtil.isNull(contact)) {
			return false;
		}

		String name;
		Room room = roomCache.get(request.getRoomId());
		if (room.getType().equals(RoomTypeEnum.GROUP.getType())) {
			// 1. 把群成员的信息设置为禁止
			name = roomGroupCache.get(request.getRoomId()).getName();
			groupMemberDao.setMemberDeFriend(request.getRoomId(), uid, request.getState());
		} else {
			// 2. 把两个人的房间全部设置为禁止
			RoomFriend roomFriend = roomFriendCache.get(request.getRoomId());
			roomService.updateState(uid.equals(roomFriend.getUid1()), roomFriend.getUid1(), roomFriend.getUid2(), request.getState());

			name = userSummaryCache.get(roomFriend.getUid1().equals(uid) ? roomFriend.getUid2() : roomFriend.getUid1()).getName();
		}

		// 3. 通知所有设备我已经屏蔽这个房间
		pushService.sendPushMsg(WsAdapter.buildShieldContact(request.getState(), name), uid, uid);
		roomFriendCache.delete(request.getRoomId());
		contact.setShield(request.getState());
		return contactDao.updateById(contact);
	}

	@Override
	public void mergeMessage(Long uid, MergeMessageReq req) {
		// 1. 校验人员是否在群里、或者有没有对方的好友
		Room room = roomCache.get(req.getFromRoomId());
		if (ObjectUtil.isNull(room)) {
			throw new BizException("房间不存在");
		}

		if (room.getType().equals(RoomTypeEnum.GROUP.getType())) {
			RoomGroup sourceRoomGroup = roomGroupCache.get(req.getFromRoomId());
			verifyGroupPermissions(uid, sourceRoomGroup);
		} else {
			RoomFriend roomFriend = roomFriendCache.get(req.getFromRoomId());
			if (ObjectUtil.isNull(roomFriend) || !roomFriend.getUid1().equals(uid) && !roomFriend.getUid1().equals(uid)) {
				throw new BizException("你们不是好友关系");
			}
		}

		// 2. 当是转发单条消息的时候
		List<Message> messagess = chatService.getMsgByIds(req.getMessageIds());

		// 3. 发布合并消息
		for (Long roomId : req.getRoomIds()) {
			if (req.getType().equals(MergeTypeEnum.SINGLE.getType())) {
				messagess.forEach(message -> {
					ChatMessageReq messageReq = new ChatMessageReq();
					messageReq.setMsgType(message.getType());
					messageReq.setRoomId(roomId);
					// 扩展消息需要另外解析
					messageReq.setBody(analyze(message));
					messageReq.setSkip(true);
					chatService.sendMsg(messageReq, uid);
				});
			} else {
				chatService.sendMsg(MessageAdapter.buildMergeMsg(roomId, messagess), uid);
			}
		}
	}

	/**
	 * 解析转发的消息
	 *
	 * @param message 数据库里面的消息实体
	 */
	private Object analyze(Message message) {
		return switch (MessageTypeEnum.of(message.getType())) {
			case TEXT -> {
				TextMsgReq textMsgReq = new TextMsgReq();
				textMsgReq.setContent(message.getContent());
				textMsgReq.setReplyMsgId(message.getReplyMsgId());
				textMsgReq.setAtUidList(message.getExtra().getAtUidList());
				yield textMsgReq;
			}
			case RECALL -> message.getExtra().getRecall();
			case IMG -> message.getExtra().getImgMsgDTO();
			case FILE -> message.getExtra().getFileMsg();
			case SOUND -> message.getExtra().getSoundMsgDTO();
			case VIDEO -> message.getExtra().getVideoMsgDTO();
			case EMOJI -> message.getExtra().getEmojisMsgDTO();
			case AI, REPLY, AIT, MIXED, BOT, SYSTEM -> null;
			case MERGE -> message.getExtra().getMergeMsgDTO();
			case NOTICE -> message.getExtra().getNoticeMsgDTO();
			case VIDEO_CALL -> message.getExtra().getVideoCallMsgDTO();
			case AUDIO_CALL -> message.getExtra().getAudioCallMsgDTO();
			case LOCATION -> message.getExtra().getMapMsgDTO();
		};
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

		Map<Long, Long> map = presenceApi.getBatchGroupOnlineCounts(Arrays.asList(room.getId())).getData();

		// 获取群成员数、在线人员、备注、我的群名称
		Long onlineNum = map.get(room.getId());
		Long memberNum = (long) groupMemberCache.getMemberUidList(roomId).size();
		GroupMember member = groupMemberDao.getMemberByGroupId(roomGroup.getId(), uid);

		return MemberResp.builder()
				.avatar(roomGroup.getAvatar())
				.roomId(roomId)
				.groupName(roomGroup.getName())
				.onlineNum(onlineNum)
				.memberNum(memberNum)
				.account(roomGroup.getAccount())
				.remark(member == null? "": member.getRemark())
				.myName(member == null? "": member.getMyName())
				.allowScanEnter(roomGroup.getAllowScanEnter())
				.roleId(getGroupRole(uid, roomGroup.getId()))
				.build();
	}

	@Override
	public List<ChatMemberResp> listMember(MemberReq request) {
		// 1. 基础校验
		Room room = roomCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(room, "房间号有误");
		if (RoomTypeEnum.FRIEND.getType().equals(room.getType())) {
			throw new BizException("当前房间非群聊");
		}

		// 2. 获取群组和成员数据
		List<ChatMemberResp> chatMemberResps = groupMemberDao.getMemberListByGroupId(roomGroupCache.get(request.getRoomId()).getId());

		// 3. 批量获取用户信息
		List<Long> uids = chatMemberResps.stream().map(ChatMemberResp::getUid).map(Long::parseLong).collect(Collectors.toList());
		Map<Long, SummeryInfoDTO> batch = userSummaryCache.getBatch(uids);

		// 5. 批量获取在线状态
		Set<Long> onlineList = presenceApi.getOnlineUsersList(new ArrayList<>(uids)).getData();

		// 6. 填充用户信息和在线状态
		chatMemberResps.forEach(item -> {
			Long uid = Long.parseLong(item.getUid());
			SummeryInfoDTO user = batch.get(uid);

			if (user != null) {
				item.setActiveStatus(onlineList.contains(uid) ? ChatActiveStatusEnum.ONLINE.getStatus() : ChatActiveStatusEnum.OFFLINE.getStatus());
				item.setLastOptTime(user.getLastOptTime());
				item.setName(user.getName());
				item.setAvatar(user.getAvatar());
				item.setLocPlace(user.getLocPlace());
				item.setAccount(user.getAccount());
				item.setUserStateId(user.getUserStateId());
				item.setItemIds(user.getItemIds());
				item.setUserType(user.getUserType());
				item.setWearingItemId(user.getWearingItemId());
			}
		});

		// 7. 群主、管理员永远在前面
		return chatMemberResps;
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
			Map<Long, User> batch = userCache.getBatch(memberUidList);
			return MemberAdapter.buildMemberList(batch);
		}
	}

	@Override
	@RedissonLock(prefixKey = "delMember:", key = "#request.roomId")
	public void delMember(Long uid, MemberDelReq request) {
		Room room = roomCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(room, "房间号有误");
		RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(roomGroup, "房间号有误");
		GroupMember self = groupMemberDao.getMemberByGroupId(roomGroup.getId(), uid);
		AssertUtil.isNotEmpty(self, GroupErrorEnum.USER_NOT_IN_GROUP, "");

		// 如果房间人员小于3人 那么直接解散群聊
		CacheKey membersKey = PresenceCacheKeyBuilder.groupMembersKey(request.getRoomId());
		Long memberNum = cachePlusOps.sCard(membersKey);
		if (memberNum <= 3) {
			MemberExitReq exitReq = new MemberExitReq();
			exitReq.setRoomId(request.getRoomId());
			exitReq.setAccount(roomGroup.getAccount());
			exitGroup(true, uid, exitReq);
			return;
		}

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
		GroupMember member = groupMemberDao.getMemberByGroupId(roomGroup.getId(), removedUid);
		AssertUtil.isNotEmpty(member, "用户已经移除");

		// 发送移除事件告知群成员
		if (transactionTemplate.execute(e -> {
			groupMemberDao.removeById(member.getId());
			// 1.5 移除会话
			contactDao.removeByRoomId(room.getId(), Collections.singletonList(request.getUid()));
			return true;
		})) {
			// 移除群聊缓存
			CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(request.getUid());
			cachePlusOps.sRem(membersKey, request.getUid());
			cachePlusOps.sRem(uKey, room.getId());
			asyncOnline(Arrays.asList(request.getUid()), room.getId(), false);

			// 推送状态到前端
			List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
			if (!memberUidList.contains(request.getUid())) {
				memberUidList.add(request.getUid());
			}
			WsBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), (int) (memberNum - 1), Math.toIntExact(cachePlusOps.sCard(PresenceCacheKeyBuilder.onlineGroupMembersKey(room.getId()))), Arrays.asList(member.getUid()), WSMemberChange.CHANGE_TYPE_REMOVE);
			pushService.sendPushMsg(ws, memberUidList, uid);
			groupMemberCache.evictMemberList(room.getId());
			groupMemberCache.evictExceptMemberList(room.getId());
			groupMemberCache.evictMemberDetail(room.getId(), removedUid);

			long uuid = uidGenerator.getUid();
			// 保存被删除人的通知
			noticeService.createNotice(
					RoomTypeEnum.GROUP,
					NoticeTypeEnum.GROUP_MEMBER_DELETE,
					uid,
					removedUid,
					uuid,
					removedUid,
					roomGroup.getName(),
					roomGroup.getName()
			);

			// 获取所有管理员
			List<Long> managerIds = groupMemberDao.getGroupUsers(roomGroup.getId(), true);
			managerIds.forEach(managerId -> noticeService.createNotice(
					RoomTypeEnum.GROUP,
					NoticeTypeEnum.GROUP_MEMBER_DELETE,
					uid,
					managerId,
					uuid,
					removedUid,
					roomGroup.getName(),
					roomGroup.getName()
			));
		}
	}

	@Override
	@RedissonLock(key = "#request.roomId")
	public void addMember(Long uid, MemberAddReq request) {
		// 1. 校验数据
		Room room = roomCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(room, "房间号有误");
		RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(roomGroup, "房间号有误");
		GroupMember self = groupMemberDao.getMemberByGroupId(roomGroup.getId(), uid);
		AssertUtil.isNotEmpty(self, "您不是群成员");
		// 已经进群了的
		List<Long> memberBatch = groupMemberDao.getMemberBatch(roomGroup.getId(), request.getUidList()).stream().map(GroupMember::getUid).toList();
		// 已经邀请过的数据
		List<Long> existingUsers = userApplyDao.getExistingUsers(request.getRoomId(), request.getUidList());
		HashSet<Long> validUidSet = request.getUidList();
		validUidSet.removeAll(memberBatch);
		validUidSet.removeAll(existingUsers);

		List<Long> validUids = new ArrayList<>(validUidSet);
		if (CollectionUtils.isEmpty(validUids)) {
			return;
		}

		// 2. 创建邀请记录
		List<UserApply> invites = validUids.stream().map(inviteeUid -> new UserApply(uid, RoomTypeEnum.GROUP.getType(), roomGroup.getRoomId(), inviteeUid, StrUtil.format("{}邀请你加入{}", userSummaryCache.get(uid).getName(), roomGroup.getName()), ApplyStatusEnum.WAIT_APPROVAL.getCode(), UNREAD.getCode(), 0, false)).collect(Collectors.toList());
		transactionTemplate.execute(e -> userApplyDao.saveBatch(invites));

		// 3. 通知被邀请的人进群, 通知时绑定通知id
		List<Long> managerIds = groupMemberDao.getGroupUsers(roomGroup.getId(), true);
		invites.forEach(invite -> {
			SummeryInfoDTO user = userSummaryCache.get(invite.getTargetId());
			if (ObjectUtil.isNotNull(user)) {
				pushService.sendPushMsg(MessageAdapter.buildInviteeUserAddGroupMessage(noticeDao.getUnReadCount(invite.getTargetId(), invite.getTargetId())), invite.getTargetId(), uid);
			}

			// 每个被邀请的人都要收到邀请进群的消息
			noticeService.createNotice(
					RoomTypeEnum.GROUP,
					NoticeTypeEnum.GROUP_INVITE_ME,
					uid,
					invite.getTargetId(),
					invite.getId(),
					invite.getTargetId(),
					roomGroup.getName(),
					roomGroup.getName()
			);

			// 每个管理员都要收到邀请进群的消息
			managerIds.forEach(managerId -> noticeService.createNotice(
					RoomTypeEnum.GROUP,
					NoticeTypeEnum.GROUP_INVITE,
					uid,
					managerId,
					invite.getId(),
					invite.getTargetId(),
					roomGroup.getName(),
					roomGroup.getName()
			));
		});
	}

	/**
	 * 退出群聊 | 解散群聊
	 *
	 * @param uid     需要退出的用户ID
	 * @param request 请求信息
	 */
	@Override
	@RedissonLock(prefixKey = "exitGroup:", key = "#request.roomId")
	public void exitGroup(Boolean isGroup, Long uid, MemberExitReq request) {
		Long roomId = request.getRoomId();
		// 1. 判断群聊是否存在
		RoomGroup roomGroup = roomGroupCache.getByRoomId(roomId);
		AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

		// 2. 判断房间是否是大群聊 （大群聊禁止退出）
		Room room = roomService.getById(roomId);
		AssertUtil.isFalse(room.isHotRoom(), GroupErrorEnum.NOT_ALLOWED_FOR_EXIT_GROUP);

		// 3. 判断群成员是否在群中
		Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), Collections.singletonList(uid));
		AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

		// 5. 获取要移除的群成员
		Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
		List<Long> memberUidList;
		if (isLord) {
			memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId(), null);
		} else {
			memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
		}
		CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(room.getId());
		if (isGroup || isLord) {
			User user = userCache.get(uid);
			ChatMessageReq messageReq = new ChatMessageReq();
			messageReq.setBody(StrUtil.format("{}解散了群聊", user.getName()));
			messageReq.setMsgType(MessageTypeEnum.SYSTEM.getType());
			chatService.sendMsg(messageReq, uid);

			// 4.1 删除房间和群并清除缓存
			transactionTemplate.execute(e -> {
				boolean isDelRoom = roomService.removeById(roomId);
				roomGroupCache.removeById(roomGroup.getId());
				roomGroupCache.evictGroup(roomGroup.getAccount());
				if (StrUtil.isNotEmpty(request.getAccount())) {
					roomGroupCache.evictGroup(request.getAccount());
				}
				AssertUtil.isTrue(isDelRoom, ResponseEnum.SYSTEM_BUSY.getMsg());
				// 4.2 删除会话
				Boolean isDelContact = contactDao.removeByRoomId(roomId, Collections.EMPTY_LIST);
				AssertUtil.isTrue(isDelContact, "会话移除异常");
				// 4.3 删除群成员
				Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.EMPTY_LIST);
				AssertUtil.isTrue(isDelGroupMember, "群成员移除失败");
				// 4.4 删除消息记录 (逻辑删除)
				Boolean isDelMessage = messageDao.removeByRoomId(roomId, Collections.EMPTY_LIST);
				AssertUtil.isTrue(isDelMessage, ResponseEnum.SYSTEM_BUSY.getMsg());
				return true;
			});
			// 4.5 告知所有人群已经被解散, 这里要走groupMemberDao查询，缓存中可能没有屏蔽群的用户
			groupMemberCache.evictMemberList(room.getId());
			groupMemberCache.evictExceptMemberList(room.getId());
			groupMemberCache.evictAllMemberDetails();
			// 新版解散群聊
			CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
			cachePlusOps.del(uKey, gKey);
			asyncOnline(memberUidList, room.getId(), false);
//			pushService.sendPushMsg(RoomAdapter.buildGroupDissolution(roomGroup.getRoomId()), memberUidList, uid);
		} else {
			// 如果房间人员小于3人 那么直接解散群聊
			if (cachePlusOps.sCard(gKey) <= 3) {
				MemberExitReq exitReq = new MemberExitReq();
				exitReq.setRoomId(request.getRoomId());
				exitReq.setAccount(roomGroup.getAccount());
				exitGroup(true, uid, exitReq);
				return;
			}

			if (transactionTemplate.execute(e -> {
				// 4.6 删除会话
				Boolean isDelContact = contactDao.removeByRoomId(roomId, Collections.singletonList(uid));
				AssertUtil.isTrue(isDelContact, "会话移除异常");
				// 4.7 删除群成员
				Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.singletonList(uid));
				AssertUtil.isTrue(isDelGroupMember, "群成员移除失败");
				return true;
			})) {
				// 新版退出群聊
				CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(uid);

				cachePlusOps.sRem(gKey, uid);
				cachePlusOps.sRem(uKey, room.getId());
				asyncOnline(Arrays.asList(uid), room.getId(), false);

				// 4.8 发送移除事件告知群成员
				WsBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), Math.toIntExact(cachePlusOps.sCard(gKey)), Math.toIntExact(cachePlusOps.sCard(PresenceCacheKeyBuilder.onlineGroupMembersKey(room.getId()))), Arrays.asList(uid), WSMemberChange.CHANGE_TYPE_QUIT);
				pushService.sendPushMsg(ws, memberUidList, uid);
				groupMemberCache.evictMemberList(room.getId());
				groupMemberCache.evictExceptMemberList(room.getId());
				groupMemberCache.evictMemberDetail(room.getId(), uid);
			}
		}
	}

	@Override
	@RedissonLock(prefixKey = "addGroup:", key = "#uid")
	public Long addGroup(Long uid, GroupAddReq request) {
		Map<Long, SummeryInfoDTO> userMap = userSummaryCache.getBatch(request.getUidList());
		AssertUtil.isTrue(userMap.size() > 1, "群聊人数应大于2人");

		List<Long> uidList = new ArrayList<>(userMap.keySet());
		AtomicReference<Long> roomIdAtomic = new AtomicReference(0L);

		// 创建群组数据并推送数据到前端
		if (transactionTemplate.execute(e -> {
			RoomGroup roomGroup = roomService.createGroupRoom(uid, request);
			// 批量保存群成员
			List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(uidList, roomGroup.getId());
			groupMemberDao.saveBatch(groupMembers);
			roomIdAtomic.set(roomGroup.getRoomId());
			return true;
		})) {
			// 发送邀请加群消息 ==> 触发每个人的会话
			roomGroupCache.evictAllCaches();
			// 处理新房间里面所有在线人员
			uidList.add(uid);
			groupMemberCache.evictMemberList(roomIdAtomic.get());
			groupMemberCache.evictExceptMemberList(roomIdAtomic.get());

			// 更新在线缓存
			CacheKey onlineGroupMembersKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomIdAtomic.get());
			CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(roomIdAtomic.get());
			uidList.forEach(id -> {
				cachePlusOps.sAdd(gKey, id);
				cachePlusOps.sAdd(PresenceCacheKeyBuilder.userGroupsKey(id), roomIdAtomic.get());
			});
			asyncOnline(uidList, roomIdAtomic.get(), true);
			SpringUtils.publishEvent(new GroupMemberAddEvent(this, roomIdAtomic.get(), Math.toIntExact(cachePlusOps.sCard(gKey)), Math.toIntExact(cachePlusOps.sCard(onlineGroupMembersKey)), request.getUidList(), uid));
		}
		return roomIdAtomic.get();
	}

	private boolean hasPower(GroupMember self) {
		return Objects.equals(self.getRoleId(), GroupRoleEnum.LEADER.getType())
				|| Objects.equals(self.getRoleId(), GroupRoleEnum.MANAGER.getType())
				|| roleService.hasRole(self.getUid(), RoleTypeEnum.ADMIN);
	}

	/**
	 * 获取群角色
	 */
	private Integer getGroupRole(Long uid, Long groupId) {
		GroupMember member = Objects.isNull(uid) ? null : groupMemberDao.getMemberByGroupId(groupId, uid);
		if (Objects.nonNull(member)) {
			return GroupRoleAPPEnum.of(member.getRoleId()).getType();
		} else {
			return GroupRoleAPPEnum.REMOVE.getType();
		}
	}

	private boolean isHotGroup(Room room) {
		return HotFlagEnum.YES.getType().equals(room.getHotFlag());
	}

	private Double getCursorOrNull(String cursor) {
		if (StringUtils.isEmpty(cursor)) {
			return null;
		}
		return Optional.of(cursor).map(Double::parseDouble).orElse(null);
	}

	/**
	 * @param contactMap 会话映射
	 * @param uid        当前登录的用户
	 * @param roomIds    所有会话对应的房间
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
		// 消息未读数
		Map<Long, Integer> unReadCountMap = getUnReadCountMap(contactMap.values());

		return roomBaseInfoMap.values().stream().map(room -> {
					ChatRoomResp resp = new ChatRoomResp();
					Long roomId = room.getRoomId();
					RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(roomId);
					Contact contact = contactMap.get(StrUtil.format("{}_{}", uid, roomId));
					if (ObjectUtil.isNotNull(contact)) {
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
					resp.setId(contact.getId());
					resp.setDetailId(room.getId());
					resp.setAvatar(roomBaseInfo.getAvatar());
					resp.setRoomId(roomId);
					resp.setAccount(room.getAccount());
					resp.setActiveTime(room.getActiveTime());
					resp.setHotFlag(roomBaseInfo.getHotFlag());
					resp.setType(roomBaseInfo.getType());
					resp.setName(roomBaseInfo.getName());
					resp.setOperate(roomBaseInfo.getRoleId());
					resp.setRemark(roomBaseInfo.getRemark());
					resp.setMyName(roomBaseInfo.getMyName());
					Message message = msgMap.get(room.getLastMsgId());
					if (resp.getShield()) {
						resp.setText("您已屏蔽该会话");
					} else {
						if (Objects.nonNull(message)) {
							AbstractMsgHandler strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
							// 判断是群聊还是单聊
							if (Objects.equals(roomBaseInfo.getType(), RoomTypeEnum.GROUP.getType())) {
								resp.setText(strategyNoNull.showContactMsg(message));
								GroupMember messageUser = groupMemberCache.getMemberDetail(roomId, message.getFromUid());
								if (ObjectUtil.isNotNull(messageUser)) {
									// 当自己查看时，且最后一条消息是自己发送的，那么显示群备注
									if (uid.equals(message.getFromUid()) && StrUtil.isNotEmpty(messageUser.getRemark())) {
										resp.setRemark(messageUser.getRemark());
									}
								}
							} else {
								resp.setText(strategyNoNull.showContactMsg(message));
							}
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
	public Map<Long, Integer> getUnReadCountMap(Collection<Contact> contactList) {
		if (CollUtil.isEmpty(contactList)) {
			return new HashMap<>();
		}

		return messageDao.batchGetUnReadCount(contactList);
	}

	/**
	 * 返回房间id与好友的映射
	 *
	 * @param roomIds
	 * @param uid
	 * @return
	 */
	private Map<Long, User> getFriendRoomMap(List<Long> roomIds, Long uid) {
		if (CollectionUtil.isEmpty(roomIds)) {
			return new HashMap<>();
		}
		Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(roomIds);
		Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(), uid);
		Map<Long, User> userBatch = userCache.getBatch(new ArrayList<>(friendUidSet));
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
		Map<Long, RoomBaseInfo> collect = roomMap.values().stream().filter(Objects::nonNull).map(room -> {
			RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
			roomBaseInfo.setRoomId(room.getId());
			roomBaseInfo.setType(room.getType());
			roomBaseInfo.setHotFlag(room.getHotFlag());
			roomBaseInfo.setLastMsgId(room.getLastMsgId());
			roomBaseInfo.setActiveTime(room.getActiveTime());
			if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
				RoomGroup roomGroup = roomInfoBatch.get(room.getId());
				roomBaseInfo.setId(roomGroup.getId());
				roomBaseInfo.setAvatar(roomGroup.getAvatar());
				roomBaseInfo.setAccount(roomGroup.getAccount());
				GroupMember member = groupMemberCache.getMemberDetail(room.getId(), uid);
				// todo 稳定了这里可以不用判空，理论上100% 在群里
				if (ObjectUtil.isNotNull(member)) {
					roomBaseInfo.setMyName(member.getMyName());
					roomBaseInfo.setRemark(member.getRemark());
					roomBaseInfo.setName(roomGroup.getName());
					roomBaseInfo.setRoleId(member.getRoleId());
				} else {
					roomBaseInfo.setName("会话异常");
					roomBaseInfo.setMyName("会话异常");
					roomBaseInfo.setRemark("会话异常");
					roomBaseInfo.setRoleId(0);
				}
			} else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
				User user = friendRoomMap.get(room.getId());
				roomBaseInfo.setId(user.getId());
				roomBaseInfo.setRoleId(0);
				roomBaseInfo.setName(user.getName());
				roomBaseInfo.setAvatar(user.getAvatar());
				roomBaseInfo.setAccount(user.getAccount());
			}
			return roomBaseInfo;
		}).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));

		return collect;
	}
}
