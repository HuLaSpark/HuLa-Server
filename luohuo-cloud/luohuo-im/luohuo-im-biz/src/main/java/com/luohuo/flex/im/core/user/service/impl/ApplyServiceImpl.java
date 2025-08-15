package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.common.cache.FriendCacheKeyBuilder;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.im.common.event.GroupMemberAddEvent;
import com.luohuo.flex.im.common.event.UserApplyEvent;
import com.luohuo.flex.im.common.event.UserApprovalEvent;
import com.luohuo.flex.im.core.chat.dao.GroupMemberDao;
import com.luohuo.flex.im.core.chat.dao.RoomGroupDao;
import com.luohuo.flex.im.core.chat.service.ChatService;
import com.luohuo.flex.im.core.chat.service.RoomAppService;
import com.luohuo.flex.im.core.chat.service.RoomService;
import com.luohuo.flex.im.core.chat.service.adapter.MemberAdapter;
import com.luohuo.flex.im.core.chat.service.adapter.MessageAdapter;
import com.luohuo.flex.im.core.chat.service.cache.GroupMemberCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomCache;
import com.luohuo.flex.im.core.chat.service.cache.RoomGroupCache;
import com.luohuo.flex.im.core.user.dao.UserApplyDao;
import com.luohuo.flex.im.core.user.dao.UserFriendDao;
import com.luohuo.flex.im.core.user.service.ApplyService;
import com.luohuo.flex.im.core.user.service.FriendService;
import com.luohuo.flex.im.core.user.service.adapter.FriendAdapter;
import com.luohuo.flex.im.core.user.service.adapter.WsAdapter;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.domain.dto.RequestApprovalDto;
import com.luohuo.flex.im.domain.entity.GroupMember;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.domain.entity.RoomFriend;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.entity.UserFriend;
import com.luohuo.flex.im.domain.enums.ApplyDeletedEnum;
import com.luohuo.flex.im.domain.enums.ApplyEnum;
import com.luohuo.flex.im.domain.enums.ApplyStatusEnum;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.domain.vo.req.PageBaseReq;
import com.luohuo.flex.im.domain.vo.req.friend.FriendApplyReq;
import com.luohuo.flex.im.domain.vo.req.friend.FriendApproveReq;
import com.luohuo.flex.im.domain.vo.request.RoomApplyReq;
import com.luohuo.flex.im.domain.vo.request.member.ApplyReq;
import com.luohuo.flex.im.domain.vo.request.member.GroupApplyHandleReq;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendApplyResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendUnreadResp;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.luohuo.flex.im.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;

/**
 * 好友申请、群聊邀请
 * @author 乾乾
 */
@Slf4j
@Service
@AllArgsConstructor
public class ApplyServiceImpl implements ApplyService {

	private final RoomGroupDao roomGroupDao;
    private UserFriendDao userFriendDao;
    private UserApplyDao userApplyDao;
    private RoomService roomService;
    private ChatService chatService;
	private RoomCache roomCache;
	private PushService pushService;
	private UserCache userCache;
	private GroupMemberCache groupMemberCache;
	private RoomGroupCache roomGroupCache;
	private GroupMemberDao groupMemberDao;
	private FriendService friendService;
	private CachePlusOps cachePlusOps;
	private RoomAppService roomAppService;

    /**
     * 申请好友
     *
     * @param request 请求
     */
    @Override
	@RedissonLock(prefixKey = "friend:apply", key = "#uid")
	@Transactional(rollbackFor = Exception.class)
    public UserApply apply(Long uid, FriendApplyReq request) {
        //是否有好友关系
        UserFriend friend = userFriendDao.getByFriend(uid, request.getTargetUid());
        AssertUtil.isEmpty(friend, "你们已经是好友了");
        // 是否有待审批的申请记录(自己的)
        UserApply selfApproving = userApplyDao.getFriendApproving(uid, request.getTargetUid(), true);
        if (Objects.nonNull(selfApproving)) {
            log.info("已有好友申请记录,uid:{}, targetId:{}", uid, request.getTargetUid());
            return null;
        }
        // 是否有待审批的申请记录(别人请求自己的)
        UserApply friendApproving = userApplyDao.getFriendApproving(request.getTargetUid(), uid, false);
        if (Objects.nonNull(friendApproving)) {
			acceptInvite(uid, new ApplyReq(friendApproving.getId(), ApplyStatusEnum.AGREE.getCode()));
            return null;
        }
        // 申请入库
        UserApply newApply = FriendAdapter.buildFriendApply(uid, request);
        userApplyDao.save(newApply);
        // 申请事件
        SpringUtils.publishEvent(new UserApplyEvent(this, newApply));
		return newApply;
    }

	/**
	 * 申请加群
	 *
	 * @param req
	 */
	@Override
	@RedissonLock(key = "#uid")
	public Boolean applyGroup(Long uid, RoomApplyReq req){
		// 获取到申请加入的群聊
		RoomGroup roomGroup = roomGroupDao.searchGroupByAccount(req.getAccount());
		if(ObjectUtil.isNull(roomGroup)){
			return false;
		}

		List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
		if(memberUidList.contains(uid)){
			throw new BizException("你已经加入群聊");
		}

		// 获取到这个群的管理员的人的信息
		List<Long> groupAdminIds = roomService.getGroupUsers(roomGroup.getId(), true);
		User userInfo = userCache.getUserInfo(uid);
		String msg = StrUtil.format("用户{}申请加入群聊{}", userInfo.getName(), roomGroup.getName());

		for (Long groupAdminId : groupAdminIds) {
			friendService.createUserApply(uid, roomGroup.getRoomId(), groupAdminId, msg, 2);

			// 给群里的管理员发送申请
			pushService.sendPushMsg(MessageAdapter.buildRoomGroupMessage(uid, roomGroup.getRoomId(), groupAdminId, msg), groupAdminId, uid);
		}
		return true;
	}

	@Override
	@RedissonLock(key = "#request.roomId")
	@Transactional(rollbackFor = Exception.class)
	public void acceptInvite(Long uid, ApplyReq request) {
		// 1. 校验邀请记录
		UserApply invite = userApplyDao.getById(request.getApplyId());
		if (invite == null || !invite.getTargetId().equals(uid)) {
			throw new BizException("无效的邀请");
		}

		if (!invite.getStatus().equals(ApplyStatusEnum.WAIT_APPROVAL.getCode())) {
			throw new BizException("无效的审批");
		}

		// 处理加好友
		invite.setStatus(request.getState());
		if(invite.getType().equals(ApplyEnum.USER_FRIEND.getCode())){
			AssertUtil.equal(invite.getStatus(), WAIT_APPROVAL.getCode(), "已同意好友申请");
			// 同意申请
			userApplyDao.agree(request.getApplyId());

			// 检查是否是临时会话升级
			UserFriend userFriend = userFriendDao.getByFriend(uid, invite.getUid());
			boolean isFromTempSession = userFriend != null && userFriend.getIsTemp();
			// 如果是从临时会话升级，则修改会话状态；否则创建新会话
			if (isFromTempSession) {
				userFriend.setIsTemp(false);
				userFriendDao.updateById(userFriend);
			} else {
				// 创建一个聊天房间
				RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, invite.getUid()));

				// 创建双方好友关系
				friendService.createFriend(roomFriend.getRoomId(), uid, invite.getUid());

				// 添加双方好友主动关系、被动关系
				cachePlusOps.sAdd(FriendCacheKeyBuilder.userFriendsKey(uid), invite.getUid());
				cachePlusOps.sAdd(FriendCacheKeyBuilder.reverseFriendsKey(invite.getUid()), uid);
				cachePlusOps.sAdd(FriendCacheKeyBuilder.userFriendsKey(invite.getUid()), uid);
				cachePlusOps.sAdd(FriendCacheKeyBuilder.reverseFriendsKey(uid), invite.getUid());
				friendService.warmUpRoomMemberCache(Arrays.asList(roomFriend.getRoomId()));

				// 发送一条同意消息。。我们已经是好友了，开始聊天吧
				chatService.sendMsg(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId(), true), uid);
			}

			// 通知请求方已处理好友申请
			SpringUtils.publishEvent(new UserApprovalEvent(this, RequestApprovalDto.builder().uid(uid).targetUid(invite.getUid()).build()));
		} else {
			// 处理加群
			groupMemberDao.save(MemberAdapter.buildMemberAdd(invite.getTargetId(), invite.getTargetId()));

			// 3.2 系统下发加群信息
			Room room = roomCache.get(invite.getRoomId());
			if(room.isHotRoom()){
				friendService.createSystemFriend(request.getApplyId());
			}

			// 3.3 写入缓存
			CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(invite.getRoomId());
			CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(room.getId());
			cachePlusOps.sAdd(uKey, room.getId());
			cachePlusOps.sAdd(gKey, invite.getTargetId());
			roomAppService.asyncOnline(Arrays.asList(invite.getTargetId()), room.getId(), true);

			SpringUtils.publishEvent(new GroupMemberAddEvent(this, room.getId(), Arrays.asList(request.getApplyId()), uid));
		}

		// 2.1 更新邀请状态
		userApplyDao.updateById(invite);
	}

	/**
	 * 处理加群申请
	 * @param uid 当前登录人id
	 * @param req 审批请求
	 */
	@Override
	@RedissonLock(key = "#request.applyId")
	@Transactional(rollbackFor = Exception.class)
	public void handleApply(Long uid, GroupApplyHandleReq req) {
		// 1. 校验申请记录
		UserApply apply = userApplyDao.getById(req.getApplyId());
		if (apply == null || !apply.getType().equals(2)) {
			throw new BizException("无效的申请记录");
		}

		// 2. 校验管理员权限
		RoomGroup group = roomGroupCache.get(apply.getRoomId());
		GroupMember member = groupMemberDao.getMember(group.getId(), uid);
		if (member == null || (member.getRoleId().equals(GroupRoleEnum.LEADER.getType()) &&
				!member.getRoleId().equals(GroupRoleEnum.MANAGER.getType()))) {
			throw new BizException("无审批权限");
		}

		// 3. 更新申请状态
		apply.setStatus(req.getStatus());
		apply.setReadStatus(1);
		userApplyDao.updateById(apply);

		// 5. 同意则加群
		if (req.getStatus().equals(2)) {
			// 5.1 加入群聊
			groupMemberDao.save(MemberAdapter.buildMemberAdd(group.getId(), apply.getUid()));

			// 5.2 处理热点群机器人好友
			Room room = roomCache.get(group.getRoomId());
			if (room.isHotRoom()) {
				friendService.createSystemFriend(apply.getUid());
			}

			// 5.3 写入缓存
			CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(apply.getUid());
			CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(room.getId());
			cachePlusOps.sAdd(uKey, room.getId());
			cachePlusOps.sAdd(gKey, apply.getUid());
			roomAppService.asyncOnline(Arrays.asList(apply.getUid()), room.getId(), true);

			// 5.5 发布成员增加事件
			SpringUtils.publishEvent(new GroupMemberAddEvent(this, room.getId(), Collections.singletonList(apply.getUid()), uid));
		}

		// 6. 通知申请人 [这条消息需要覆盖前端]
		pushService.sendPushMsg(WsAdapter.buildApplyResultWS(apply.getUid(), group.getRoomId(), uid, apply.getMsg(), apply.getStatus(), apply.getReadStatus()), apply.getUid(), uid);
	}

	/**
     * 分页查询好友申请
     *
     * @param request 请求
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
        IPage<UserApply> userApplyIPage = userApplyDao.friendApplyPage(uid, request.plusPage());
        if (CollectionUtil.isEmpty(userApplyIPage.getRecords())) {
            return PageBaseResp.empty();
        }
        // 将这些申请列表设为已读
        readApples(uid, userApplyIPage);
        // 返回消息
        return PageBaseResp.init(userApplyIPage, FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    private void readApples(Long uid, IPage<UserApply> userApplyIpage) {
        List<Long> applyIds = userApplyIpage.getRecords()
                .stream().map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.readApples(uid, applyIds);
    }

    /**
     * 申请未读数
     *
     * @return {@link FriendUnreadResp}
     */
    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    @Override
    public void reject(Long uid, FriendApproveReq request) {
        UserApply userApply = checkRecord(request);
        userApplyDao.updateStatus(request.getApplyId(), ApplyStatusEnum.REJECT);
    }

    @Override
    public void ignore(Long uid, FriendApproveReq request) {
        checkRecord(request);
        userApplyDao.updateStatus(request.getApplyId(), ApplyStatusEnum.IGNORE);
    }

    @Override
	@RedissonLock(prefixKey = "friend:deleteApprove", key = "#uid")
    public void deleteApprove(Long uid, FriendApproveReq request) {
        UserApply userApply = checkRecord(request);
        ApplyDeletedEnum deletedEnum;
        //判断谁删了这条记录
        if (Objects.equals(userApply.getUid(), uid)) {
            deletedEnum = ApplyDeletedEnum.APPLY_DELETED;
        } else {
            deletedEnum = ApplyDeletedEnum.TARGET_DELETED;
        }
        //数据库已经当前删除方删了
        if (Objects.equals(userApply.getDeleted(), deletedEnum.getCode())) {
            return;
        }
        //如果之前任意一方删除过 那就是都删了
        if (!Objects.equals(userApply.getDeleted(), ApplyDeletedEnum.NORMAL.getCode())) {
            deletedEnum = ApplyDeletedEnum.ALL_DELETED;
        }
        userApplyDao.deleteApprove(request.getApplyId(), deletedEnum);
    }

    private UserApply checkRecord(FriendApproveReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getCode(), "对方已是您的好友");
        return userApply;
    }
}
