package com.luohuo.flex.im.core.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.im.api.PresenceApi;
import com.luohuo.flex.im.core.chat.dao.RoomFriendDao;
import com.luohuo.flex.im.core.chat.dao.RoomGroupDao;
import com.luohuo.flex.im.core.user.dao.UserApplyDao;
import com.luohuo.flex.im.core.user.dao.UserBackpackDao;
import com.luohuo.flex.im.core.user.dao.UserFriendDao;
import com.luohuo.flex.im.core.user.dao.UserPrivacyDao;
import com.luohuo.flex.im.domain.entity.*;
import com.luohuo.flex.im.domain.enums.ApplyEnum;
import com.luohuo.flex.im.domain.enums.ApplyStatusEnum;
import com.luohuo.flex.im.domain.vo.req.room.UserApplyResp;
import com.luohuo.flex.im.domain.vo.request.admin.AdminAddReq;
import com.luohuo.flex.im.domain.vo.request.admin.AdminRevokeReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberExitReq;
import com.luohuo.flex.model.enums.ChatActiveStatusEnum;
import com.luohuo.flex.im.domain.vo.request.contact.ContactAddReq;
import com.luohuo.flex.im.domain.vo.res.GroupListVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.luohuo.flex.im.domain.entity.msg.MergeMsg;
import com.luohuo.flex.im.domain.enums.GroupRoleAPPEnum;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.domain.enums.HotFlagEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
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
import com.luohuo.flex.model.entity.ws.ChatMessageResp;
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
import com.luohuo.flex.im.domain.enums.RoleTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.im.domain.vo.req.MergeMessageReq;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;
import com.luohuo.flex.model.entity.ws.WSMemberChange;
import com.luohuo.flex.im.core.user.service.FriendService;
import com.luohuo.flex.im.core.user.service.RoleService;
import com.luohuo.flex.im.core.user.service.adapter.WsAdapter;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.core.user.service.cache.UserInfoCache;
import com.luohuo.flex.im.core.user.service.impl.PushService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.luohuo.flex.im.common.config.ThreadPoolConfig.LUOHUO_EXECUTOR;
import static com.luohuo.flex.im.core.chat.constant.GroupConst.MAX_MANAGE_COUNT;

@Slf4j
@Service
@AllArgsConstructor
public class RoomAppServiceImpl implements RoomAppService, InitializingBean {

	private final UserBackpackDao userBackpackDao;
	private final RoomGroupDao roomGroupDao;
	private final RoomFriendDao roomFriendDao;
	private final UserApplyDao userApplyDao;
	private ContactDao contactDao;
	private RoomCache roomCache;
	private final UserFriendDao userFriendDao;
	private UserPrivacyDao userPrivacyDao;
	private RoomGroupCache roomGroupCache;
	private RoomFriendCache roomFriendCache;
	private CachePlusOps cachePlusOps;
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
	private PresenceApi presenceApi;

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
		if(CollUtil.isNotEmpty(roomFriendList)) {
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
		return CursorPageBaseResp.init(page, result,0L);
	}

	/**
	 * 返回当前登录用户的全部会话
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
	public IPage<GroupListVO> groupList(Long uid, IPage<GroupListVO>  page) {
		roomService.groupList(uid,page);
		return page;
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
	}

	/**
	 * 分页查询加群申请记录
	 * @param uid 登录用户id
	 * @param req 分页请求
	 */
	@Override
	public CursorPageBaseResp<UserApplyResp> queryApplyPage(Long uid, MemberReq req) {
		// 1. 校验权限
		RoomGroup group = roomGroupCache.get(req.getRoomId());
		GroupMember member = groupMemberDao.getMember(group.getId(), uid);
		if (member == null || member.getRoleId() == GroupRoleEnum.MEMBER.getType()) {
			throw new BizException("无权限查看申请列表");
		}

		// 2. 游标查询申请进群记录
		CursorPageBaseResp<UserApply> userApplyPage = userApplyDao.getApplyPage(uid, 2, req);

		return CursorPageBaseResp.init(userApplyPage, userApplyPage.getList().stream().map(apply -> {
			UserApplyResp applyResp = new UserApplyResp();
			applyResp.setType(apply.getType());
			applyResp.setUid(uid);
			applyResp.setMsg(apply.getMsg());
			applyResp.setRoomId(apply.getRoomId());
			applyResp.setTargetId(apply.getTargetId());
			applyResp.setStatus(apply.getStatus());
			applyResp.setReadStatus(apply.getReadStatus());
			applyResp.setCreateTime(apply.getCreateTime());
			return applyResp;
		}).collect(Collectors.toList()), userApplyPage.getTotal());
	}

	/**
	 * 处理用户在线状态
	 * @param uidList 需要处理的用户
	 * @param roomId 在某个房间处理
	 * @return
	 */
	@Async(LUOHUO_EXECUTOR)
	public void asyncOnline(List<Long> uidList, Long roomId, boolean online) {
		Map<Long, Boolean> map = presenceApi.getUsersOnlineStatus(uidList).getData();
		uidList = map.entrySet().stream()
				.filter(entry -> entry.getValue())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		if(uidList.isEmpty()){
			return;
		}

		CacheKey ogmKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomId);
		for (Long uid : uidList) {
			CacheKey ougKey = PresenceCacheKeyBuilder.onlineUserGroupsKey(uid);

			if(online) {
				// 处理在线的状态
				cachePlusOps.sAdd(ogmKey, uid.toString());
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

		if(GroupRoleEnum.MEMBER.getType().equals(groupMember.getRoleId())) {
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
		groupMemberCache.evictMemberDetail(roomGroup.getRoomId(), uid);

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
			announcements.setCreateTime(now);
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
			if(saved){
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
		if(CollUtil.isNotEmpty(uids)){
			AnnouncementsResp announcement = roomService.getAnnouncement(param.getId());
			if(ObjectUtil.isNull(announcement)){
				return false;
			}
			Announcements announcements = new Announcements();
			announcements.setId(param.getId());
			announcements.setRoomId(param.getRoomId());
			announcements.setContent(param.getContent());
			announcements.setTop(param.getTop());
			announcements.setCreateTime(announcement.getCreateTime());
			Boolean edit = roomService.updateAnnouncement(announcements);
			if(edit){
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

		long count = userBackpackDao.countByUidAndItemId(uid, "HuLa项目贡献者专属徽章");

		if(count == 0 && GroupRoleEnum.MEMBER.getType().equals(groupMember.getRoleId())) {
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
	public ChatMessageResp mergeMessage(Long uid, MergeMessageReq req) {
		// 1. 校验人员是否在群里、或者有没有对方的好友
		Room room = roomCache.get(req.getFromRoomId());
		if(ObjectUtil.isNull(room)){
			throw new BizException("房间不存在");
		}

		if(room.getType().equals(RoomTypeEnum.GROUP.getType())){
			RoomGroup sourceRoomGroup = roomGroupCache.get(req.getFromRoomId());
			verifyGroupPermissions(uid, sourceRoomGroup);
		} else {
			RoomFriend roomFriend = roomFriendCache.get(req.getFromRoomId());
			if(ObjectUtil.isNull(roomFriend) || !roomFriend.getUid1().equals(uid) && !roomFriend.getUid1().equals(uid)) {
				throw new BizException("你们不是好友关系");
			}
		}

		// 2. 当是转发单条消息的时候
		List<Message> messagess = chatService.getMsgByIds(req.getMessageIds());
		List<MergeMsg> msgs = messagess.stream().filter(message -> message.getRoomId().equals(req.getFromRoomId())).map(message -> new MergeMsg(message.getContent(), message.getCreateTime(), userInfoCache.get(message.getFromUid()).getName())).collect(Collectors.toUnmodifiableList());

		// 3. 发布合并消息
		Long msgId = chatService.sendMsg(MessageAdapter.buildMergeMsg(req.getRoomId(), msgs), uid);
		return chatService.getMsgResp(msgId, uid);
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
		GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);

		return MemberResp.builder()
				.avatar(roomGroup.getAvatar())
				.roomId(roomId)
				.groupName(roomGroup.getName())
				.onlineNum(onlineNum)
				.memberNum(memberNum)
				.account(roomGroup.getAccount())
				.remark(member.getRemark())
				.myName(member.getMyName())
				.roleId(getGroupRole(uid, roomGroup, room))
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
	public List<ChatMemberResp> listMember(MemberReq request) {
		// 1. 基础校验
		Room room = roomCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(room, "房间号有误");
		if (RoomTypeEnum.FRIEND.getType().equals(room.getType())) {
			throw new BizException("当前房间非群聊");
		}

		// 2. 获取群组和成员数据
		RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
		List<GroupMember> groupMembers = groupMemberDao.getMemberListByGroupId(roomGroup.getId());

		// 3. 转换为响应对象
		List<ChatMemberResp> chatMemberResps = BeanUtil.copyToList(groupMembers, ChatMemberResp.class);

		// 5. 批量获取用户信息
		Set<Long> uids = chatMemberResps.stream().map(ChatMemberResp::getUid).map(Long::parseLong).collect(Collectors.toSet());
		Map<Long, User> userInfoBatch = userCache.getUserInfoBatch(uids);

		// 6. 批量获取在线状态
		Map<Long, Boolean> onlineStatusMap = presenceApi.getUsersOnlineStatus(new ArrayList<>(uids)).getData();

		// 7. 填充用户信息和在线状态
		chatMemberResps.forEach(item -> {
			Long uid = Long.parseLong(item.getUid());
			User user = userInfoBatch.get(uid);

			if (user != null) {
				item.setActiveStatus(onlineStatusMap.getOrDefault(uid, false) ? ChatActiveStatusEnum.ONLINE.getStatus() : ChatActiveStatusEnum.OFFLINE.getStatus());
				item.setLastOptTime(user.getLastOptTime());
				item.setName(user.getName());
				item.setAvatar(user.getAvatar());
				item.setAccount(user.getAccount());
				item.setUserStateId(user.getUserStateId());
			}
		});

		// 7. 群主、管理员永远在前面
		return chatMemberResps.stream()
				.sorted((m1, m2) -> {
					// 群主 > 管理员> 普通成员
					int roleCompare = Integer.compare(m1.getRoleId(), m2.getRoleId());

					// 如果是相同角色
					if (roleCompare == 0) {
						return Integer.compare(m1.getActiveStatus(), m2.getActiveStatus());
					}
					// 不同角色：管理组始终排在普通组前面
					return roleCompare;
				}).collect(Collectors.toList());
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
	@RedissonLock(prefixKey = "delMember:", key = "#request.roomId")
	public void delMember(Long uid, MemberDelReq request) {
		Room room = roomCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(room, "房间号有误");
		RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(roomGroup, "房间号有误");
		GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
		AssertUtil.isNotEmpty(self, GroupErrorEnum.USER_NOT_IN_GROUP, "groupMember");

		// 如果房间人员小于3人 那么直接解散群聊
		Long count = cachePlusOps.sCard(PresenceCacheKeyBuilder.groupMembersKey(request.getRoomId()));
		if(count < 3){
			MemberExitReq exitReq = new MemberExitReq();
			exitReq.setRoomId(request.getRoomId());
			exitReq.setAccount(roomGroup.getAccount());
			exitGroup(uid, exitReq);
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
		GroupMember member = groupMemberDao.getMember(roomGroup.getId(), removedUid);
		AssertUtil.isNotEmpty(member, "用户已经移除");
		groupMemberDao.removeById(member.getId());
		// 发送移除事件告知群成员
		List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
		if(!memberUidList.contains(request.getUid())){
			memberUidList.add(request.getUid());
		}
		WsBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), Arrays.asList(member.getUid()));
		pushService.sendPushMsg(ws, memberUidList, uid);
		groupMemberCache.evictMemberUidList(room.getId());
		groupMemberCache.evictMemberDetail(room.getId(), removedUid);

		// 新版移除群聊
		CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
		CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(room.getId());
		cachePlusOps.sRem(gKey, uid);
		cachePlusOps.sRem(uKey, room.getId());
		asyncOnline(Arrays.asList(uid), room.getId(), false);
	}

	@Override
	@RedissonLock(key = "#request.roomId")
	@Transactional(rollbackFor = Exception.class)
	public void addMember(Long uid, MemberAddReq request) {
		// 1. 校验数据
		Room room = roomCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(room, "房间号有误");
		RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
		AssertUtil.isNotEmpty(roomGroup, "房间号有误");
		GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
		AssertUtil.isNotEmpty(self, "您不是群成员");
		List<Long> memberBatch = groupMemberDao.getMemberBatch(roomGroup.getId(), request.getUidList()).stream().map(GroupMember::getUid).toList();
		Set<Long> existUid = new HashSet<>(memberBatch);
		List<Long> validUids = request.getUidList().stream().filter(a -> !existUid.contains(a)).distinct().collect(Collectors.toList());
		if (CollectionUtils.isEmpty(validUids)) {
			return;
		}

		// 2. 创建邀请记录
		List<UserApply> invites = validUids.stream().map(inviteeUid -> new UserApply(uid, ApplyEnum.GROUP.getCode(), roomGroup.getRoomId(), inviteeUid, StrUtil.format("{}邀请你加入{}", userCache.getUserInfo(uid).getName(), roomGroup.getName()), ApplyStatusEnum.WAIT_APPROVAL.getCode(), 1, 0)).collect(Collectors.toList());
		userApplyDao.saveBatch(invites);

		// 3. 通知被邀请的人进群
		validUids.forEach(inviteId -> {
			User invite = userCache.getUserInfo(inviteId);
			if(Objects.isNull(invite)){
				pushService.sendPushMsg(MessageAdapter.buildInviteeUserAddGroupMessage(uid, inviteId, roomGroup.getId()), inviteId, uid);
			}
		});
	}

	/**
	 * 退出群聊 | 解散群聊
	 *
	 * @param uid     需要退出的用户ID
	 * @param request 请求信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@RedissonLock(prefixKey = "exitGroup:", key = "#request.roomId")
	public void exitGroup(Long uid, MemberExitReq request) {
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

		// 4. 判断该用户是否是群主
		Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
		if (isLord) {
			// 4.1 删除房间和群并清除缓存
			boolean isDelRoom = roomService.removeById(roomId);
			roomGroupCache.removeById(roomGroup.getId());
			roomGroupCache.evictGroup(roomGroup.getAccount());
			if(StrUtil.isNotEmpty(request.getAccount())){
				roomGroupCache.evictGroup(request.getAccount());
			}
			AssertUtil.isTrue(isDelRoom, ResponseEnum.SYSTEM_BUSY.getMsg());
			// 4.2 删除会话
			Boolean isDelContact = contactDao.removeByRoomId(roomId, Collections.EMPTY_LIST);
			AssertUtil.isTrue(isDelContact, ResponseEnum.SYSTEM_BUSY.getMsg());
			// 4.3 获取并删除群成员
			List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId(), null);
			Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.EMPTY_LIST);
			AssertUtil.isTrue(isDelGroupMember, ResponseEnum.SYSTEM_BUSY.getMsg());
			// 4.4 删除消息记录 (逻辑删除)
			Boolean isDelMessage = messageDao.removeByRoomId(roomId, Collections.EMPTY_LIST);
			AssertUtil.isTrue(isDelMessage, ResponseEnum.SYSTEM_BUSY.getMsg());
			// 4.5 告知所有人群已经被解散, 这里要走groupMemberDao查询，缓存中可能没有屏蔽群的用户
			groupMemberCache.evictMemberUidList(room.getId());
			groupMemberCache.evictAllMemberDetails();
			// 新版解散群聊
			CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
			CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(room.getId());
			cachePlusOps.del(uKey, gKey);
			asyncOnline(memberUidList, room.getId(), false);
			pushService.sendPushMsg(RoomAdapter.buildGroupDissolution(roomGroup.getName()), memberUidList, uid);
		} else {
			// 4.6 删除会话
			Boolean isDelContact = contactDao.removeByRoomId(roomId, Collections.singletonList(uid));
			AssertUtil.isTrue(isDelContact, ResponseEnum.SYSTEM_BUSY.getMsg());
			// 4.7 删除群成员
			Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.singletonList(uid));
			AssertUtil.isTrue(isDelGroupMember, ResponseEnum.SYSTEM_BUSY.getMsg());
			// 4.8 发送移除事件告知群成员
			List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
			WsBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), Arrays.asList(uid));
			pushService.sendPushMsg(ws, memberUidList, uid);
			groupMemberCache.evictMemberUidList(room.getId());
			groupMemberCache.evictMemberDetail(room.getId(), uid);

			// 新版退出群聊
			CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
			CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(room.getId());
			cachePlusOps.sRem(gKey, uid);
			cachePlusOps.sRem(uKey, room.getId());
			asyncOnline(Arrays.asList(uid), room.getId(), false);
		}
	}

	@Override
	@RedissonLock(prefixKey = "addGroup:", key = "#uid")
	@Transactional(rollbackFor = Exception.class)
	public Long addGroup(Long uid, GroupAddReq request) {
		List<Long> userIdList = request.getUidList();
		AssertUtil.isTrue(userIdList.size() > 1,"群聊人数应大于2人");
		RoomGroup roomGroup = roomService.createGroupRoom(uid, request);

		// 批量保存群成员
		List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(userIdList, roomGroup.getId());
		groupMemberDao.saveBatch(groupMembers);

		// 发送邀请加群消息 ==> 触发每个人的会话
		roomGroupCache.evictAllCaches();

		// 处理新房间里面所有在线人员
		List<Long> uidList = new ArrayList<>(request.getUidList());
		uidList.add(uid);

		CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(roomGroup.getRoomId());
		uidList.forEach(id -> {
			cachePlusOps.sAdd(gKey, id);
			cachePlusOps.sAdd(PresenceCacheKeyBuilder.userGroupsKey(id), roomGroup.getRoomId());
		});
		asyncOnline(uidList, roomGroup.getRoomId(), true);

		applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup.getRoomId(), request.getUidList(), uid));
		return roomGroup.getRoomId();
	}

	private boolean hasPower(GroupMember self) {
		return Objects.equals(self.getRoleId(), GroupRoleEnum.LEADER.getType())
				|| Objects.equals(self.getRoleId(), GroupRoleEnum.MANAGER.getType())
				|| roleService.hasRole(self.getUid(), RoleTypeEnum.ADMIN);
	}

	/**
	 * 获取群角色
	 */
	private Integer getGroupRole(Long uid, RoomGroup roomGroup, Room room) {
		GroupMember member = Objects.isNull(uid) ? null : groupMemberDao.getMember(roomGroup.getId(), uid);
		if (Objects.nonNull(member)) {
			return GroupRoleAPPEnum.of(member.getRoleId()).getType();
		} else if (isHotGroup(room)) {
			return GroupRoleAPPEnum.MEMBER.getType();
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
		Map<Long, RoomGroup> roomGroupMapByRoomId = roomGroupDao.listByRoomIds(roomIds).stream().collect(Collectors.toMap(RoomGroup::getRoomId, Function.identity()));

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
					if (Objects.nonNull(message)) {
						AbstractMsgHandler strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
						// 判断是群聊还是单聊
						if (Objects.equals(roomBaseInfo.getType(), RoomTypeEnum.GROUP.getType())) {
							RoomGroup roomGroup = roomGroupMapByRoomId.get(roomId);
							GroupMember messageUser = null;
							try {
								messageUser = groupMemberCache.getMemberDetail(roomGroup.getId(), message.getFromUid());
							} catch (Exception e) {
								log.error("群聊成员获取失败-->", e.getMessage());
							}

							if (ObjectUtil.isNotNull(messageUser)) {
								if (StrUtil.isNotEmpty(messageUser.getMyName())){
									resp.setText(messageUser.getMyName() + ":" + strategyNoNull.showContactMsg(message));
								}

								// 当自己查看时，且最后一条消息是自己发送的，那么显示群备注
								if (uid.equals(message.getFromUid()) && StrUtil.isNotEmpty(messageUser.getRemark())){
									resp.setRemark(messageUser.getRemark());
								}
							} else {
								resp.setText((lastMsgUidMap.get(message.getFromUid()).getName()) + ":" + strategyNoNull.showContactMsg(message));
							}
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
				.map(contact -> Pair.of(contact.getRoomId(), messageDao.getUnReadCount(contact.getRoomId(), contact.getReadTime(), uid)))
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

		Map<Long, Long> remove = new HashMap<>();

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
				roomBaseInfo.setName(roomGroup.getName());
				roomBaseInfo.setAvatar(roomGroup.getAvatar());
				roomBaseInfo.setAccount(roomGroup.getAccount());
				GroupMember member = null;
				try {
					member = groupMemberCache.getMemberDetail(roomBaseInfo.getId(), uid);
				} catch (Exception e) {
					remove.put(roomBaseInfo.getId(), uid);
				}
				// todo 稳定了这里可以不用判空，理论上100% 在群里
				if (ObjectUtil.isNotNull(member)) {
					roomBaseInfo.setMyName(member.getMyName());
					roomBaseInfo.setRemark(member.getRemark());
					roomBaseInfo.setRoleId(member.getRoleId());
				}else {
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

		if (CollectionUtil.isNotEmpty(remove)) {
			log.info("需要删除的数据", remove);
		}
		return collect;
	}
}
