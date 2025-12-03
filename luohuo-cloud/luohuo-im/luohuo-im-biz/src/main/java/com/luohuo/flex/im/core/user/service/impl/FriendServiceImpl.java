package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.luohuo.basic.cache.redis.BaseRedis;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.common.OnlineService;
import com.luohuo.flex.common.cache.FriendCacheKeyBuilder;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.common.constant.DefValConstants;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum;
import com.luohuo.flex.im.domain.enums.NoticeStatusEnum;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.entity.RoomFriend;
import com.luohuo.flex.im.domain.vo.request.friend.FriendPermissionReq;
import com.luohuo.flex.im.domain.vo.request.friend.FriendRemarkReq;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.core.chat.service.ChatService;
import com.luohuo.flex.im.core.chat.service.RoomService;
import com.luohuo.flex.im.core.chat.service.adapter.MessageAdapter;
import com.luohuo.flex.im.core.user.dao.UserApplyDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.core.user.dao.UserFriendDao;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.entity.UserFriend;
import com.luohuo.flex.im.domain.vo.req.friend.FriendCheckReq;
import com.luohuo.flex.im.domain.vo.req.friend.FriendReq;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendCheckResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendResp;
import com.luohuo.flex.im.core.user.service.FriendService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 乾乾
 */
@Slf4j
@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService, InitializingBean {

    private UserFriendDao userFriendDao;
    private UserApplyDao userApplyDao;
    private RoomService roomService;
    private ChatService chatService;
    private UserDao userDao;
	private UserSummaryCache userSummaryCache;
	private BaseRedis baseRedis;
	private final PushService pushService;
	private CachePlusOps cachePlusOps;
	private OnlineService onlineService;

	/**
	 * 注入好友关系
	 * @param uid
	 */
	private void warmUpFriendCache(Long uid) {
		// 1. 查询DB获取好友列表
		List<Long> friendIds = userFriendDao.getAllFriendIdsByUid(uid);
		// 2. 更新主动关系缓存
		CacheKey friendsKey = FriendCacheKeyBuilder.userFriendsKey(uid);
		friendIds.forEach(friendId -> cachePlusOps.sAdd(friendsKey, friendId));
		// 3. 更新反向关系缓存
		friendIds.forEach(fid -> cachePlusOps.sAdd(FriendCacheKeyBuilder.reverseFriendsKey(fid), uid));
	}

	/**
	 * 这里需要分页实现，根据情况加载
	 */
	@Override
	public void afterPropertiesSet() {
		// 分页加载所有用户ID
		List<User> list = userDao.list();
		list.parallelStream().map(User::getId).forEach(this::warmUpFriendCache);
	}

	/**
	 * 初始化系统注册总人数
	 */
	@PostConstruct
	public void initUserCount() {
		Long dbCount = userDao.count();
//		cachePlusOps.set(new CacheKey("luohuo:user:total_count"), dbCount);
		baseRedis.set("luohuo:user:total_count", dbCount);
	}

    /**
     * 检查
     * 检查是否是自己好友
     *
     * @param uid     uid
     * @param request 请求
     * @return {@link FriendCheckResp}
     */
    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
        List<UserFriend> friendList = userFriendDao.getByFriends(uid, request.getUidList());

        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendUidSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

	@Override
	public Long createUserApply(Long uid, Long roomId, Long targetId, String msg, Integer type, Integer channel) {
		UserApply userApply = new UserApply();
		userApply.setMsg(msg);
		userApply.setUid(uid);
		userApply.setType(type);
		userApply.setRoomId(roomId);
		userApply.setTargetId(targetId);
		userApply.setStatus(NoticeStatusEnum.UNTREATED.getStatus());
		userApply.setReadStatus(ApplyReadStatusEnum.UNREAD.getCode());
		userApply.setApplyFor(true);
		userApply.setJoinChannel(channel);
		userApplyDao.save(userApply);
		return userApply.getId();
	}

	@Override
	public Boolean updateRemark(Long employeeId, FriendRemarkReq request) {
		UserFriend userFriend = userFriendDao.getByFriends(employeeId, Arrays.asList(request.getTargetUid())).stream().findFirst().orElse(null);
		if (ObjectUtil.isNull(userFriend)) {
			throw new RuntimeException("你俩不是好友关系!");
		}

		userFriend.setRemark(request.getRemark());
		return userFriendDao.updateById(userFriend);
	}

	@Override
	public List<ChatMemberListResp> searchFriend(FriendReq friendReq) {
		return userSummaryCache.getFriend(friendReq.getKey());
	}

	/**
	 * 好友权限设置
	 * @param uid 操作人
	 */
	@Override
	public Boolean permissionSettings(Long uid, FriendPermissionReq request) {
		// 1. 校验好友是否存在
		UserFriend userFriend = userFriendDao.getByFriend(uid, request.getFriendId());
		AssertUtil.isNotEmpty(userFriend, "你们不是好友!");

		// 2. 修改权限
		userFriend.setHideMyPosts(request.getHideMyPosts());
		userFriend.setHideTheirPosts(request.getHideTheirPosts());
		return userFriendDao.updateById(userFriend);
	}

	@Override
	public void createSystemFriend(Long uid){
		// 创建一个聊天房间
		RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, DefValConstants.DEF_BOT_ID));
		// 创建双方好友关系
		createFriend(roomFriend.getRoomId(), uid, DefValConstants.DEF_BOT_ID);
		// 发送一条同意消息。。我们已经是好友了，开始聊天吧
		chatService.sendMsg(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId(), true), uid);
		// 系统账号在群内发送一条欢迎消息
		SummeryInfoDTO user = userSummaryCache.get(uid);
		Long total = cachePlusOps.inc("luohuo:user:total_count", 0, TimeUnit.DAYS); // 查询系统总注册人员
		chatService.sendMsg(MessageAdapter.buildAgreeMsg4Group(DefValConstants.DEF_ROOM_ID, total, user.getName()), DefValConstants.DEF_BOT_ID);
	}

    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    @Override
	@RedissonLock(prefixKey = "friend:deleteFriend", key = "#uid")
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long uid, Long friendUid) {
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, friendUid);
        if (CollectionUtil.isEmpty(userFriends)) {
            log.info("没有好友关系：{},{}", uid, friendUid);
            return;
        }
		if (DefValConstants.DEF_BOT_ID.equals(friendUid)) {
			return;
		}
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);
        // 禁用房间
        roomService.disableFriendRoom(userFriends.get(0).getRoomId(), uid, friendUid);
		removeFriendCache(uid, friendUid);

		// 删除好友的事件
		WsBaseResp<String> wsMsg = new WsBaseResp<>();
		wsMsg.setData(uid.toString());
		wsMsg.setType(WSRespTypeEnum.DELETE_FRIEND.getType());
		pushService.sendPushMsg(wsMsg, Collections.singletonList(friendUid), uid);
    }

    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
        if (CollectionUtils.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }

		List<Long> friendUids = friendPage.getList().stream().map(UserFriend::getFriendUid).collect(Collectors.toList());
		Set<Long> onlineList = onlineService.getOnlineUsersList(friendUids);

		// 批量获取好友的用户信息
		List<User> friendUsers = userDao.listByIds(friendUids);
		Map<Long, User> userMap = friendUsers.stream().collect(Collectors.toMap(User::getId, Function.identity()));

		// 构建好友响应列表，填充用户信息
		List<FriendResp> friendRespList = friendPage.getList().stream().map(userFriend -> {
			FriendResp resp = new FriendResp();
			resp.setUid(userFriend.getFriendUid());
			resp.setRemark(userFriend.getRemark());
			resp.setHideMyPosts(userFriend.getHideMyPosts());
			resp.setHideTheirPosts(userFriend.getHideTheirPosts());
			resp.setActiveStatus(onlineList.contains(userFriend.getFriendUid()) ? 1 : 2);

			// 填充用户信息
			User friendUser = userMap.get(userFriend.getFriendUid());
			if (friendUser != null) {
				resp.setName(friendUser.getName());
				resp.setAccount(friendUser.getAccount());
				resp.setAvatar(friendUser.getAvatar());
			}

			return resp;
		}).collect(Collectors.toList());

		return CursorPageBaseResp.init(friendPage, friendRespList, 0L);
    }

	public void createFriend(Long roomId, Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
		userFriend1.setRoomId(roomId);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
		userFriend2.setRoomId(roomId);
        userFriendDao.saveBatch(Lists.newArrayList(userFriend1, userFriend2));
		updateFriendCache(uid, targetUid);

		// 创建好友会话
		chatService.createContact(uid, roomId);
		chatService.createContact(targetUid, roomId);
    }

	/**
	 * 注入房间中人员信息 [群、私聊]
	 * @param roomIdList 房间集合
	 */
	@Override
	public void warmUpRoomMemberCache(List<Long> roomIdList) {
		// 3. 加入单聊的房间映射
		List<UserFriend> userFriends = userFriendDao.getAllRoomInfo(roomIdList);

		userFriends.forEach(userFriend -> {
			CacheKey cacheKey = PresenceCacheKeyBuilder.groupMembersKey(userFriend.getRoomId());
			cachePlusOps.sAdd(cacheKey, userFriend.getUid());
			cachePlusOps.sAdd(cacheKey, userFriend.getFriendUid());
		});
	}

	/**
	 * 添加好友时更新缓存
	 * @param uid1 当前用户
	 * @param uid2 好友id
	 */
	private void updateFriendCache(Long uid1, Long uid2) {
		// 1. 更新主动关系
		cachePlusOps.sAdd(FriendCacheKeyBuilder.userFriendsKey(uid1), uid2);
		cachePlusOps.sAdd(FriendCacheKeyBuilder.userFriendsKey(uid2), uid1);

		// 2. 更新反向关系
		cachePlusOps.sAdd(FriendCacheKeyBuilder.reverseFriendsKey(uid1), uid2);
		cachePlusOps.sAdd(FriendCacheKeyBuilder.reverseFriendsKey(uid2), uid1);

		// 3. 设置关系状态
		cachePlusOps.set(FriendCacheKeyBuilder.friendStatusKey(uid1, uid2), "1");
	}

	/**
	 * 删除好友时清理缓存
	 * @param uid 当前用户
	 * @param friendUid 好友id
	 */
	private void removeFriendCache(Long uid, Long friendUid) {
		cachePlusOps.sRem(FriendCacheKeyBuilder.userFriendsKey(uid), friendUid);
		cachePlusOps.sRem(FriendCacheKeyBuilder.userFriendsKey(friendUid), uid);

		cachePlusOps.sRem(FriendCacheKeyBuilder.reverseFriendsKey(uid), friendUid);
		cachePlusOps.sRem(FriendCacheKeyBuilder.reverseFriendsKey(friendUid), uid);

		cachePlusOps.set(FriendCacheKeyBuilder.friendStatusKey(uid, friendUid), "0");
	}

}
