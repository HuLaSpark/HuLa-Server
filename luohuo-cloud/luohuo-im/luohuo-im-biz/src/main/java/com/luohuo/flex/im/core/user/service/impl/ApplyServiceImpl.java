package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.common.cache.FriendCacheKeyBuilder;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.im.common.event.GroupInviteMemberEvent;
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
import com.luohuo.flex.im.core.user.service.NoticeService;
import com.luohuo.flex.im.core.user.service.adapter.FriendAdapter;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.RequestApprovalDto;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.GroupMember;
import com.luohuo.flex.im.domain.entity.Notice;
import com.luohuo.flex.im.domain.entity.Room;
import com.luohuo.flex.im.domain.entity.RoomFriend;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.entity.UserFriend;
import com.luohuo.flex.im.domain.enums.ApplyDeletedEnum;
import com.luohuo.flex.im.domain.enums.NoticeStatusEnum;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.domain.enums.NoticeTypeEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import com.luohuo.flex.im.domain.vo.req.friend.FriendApplyReq;
import com.luohuo.flex.im.domain.vo.request.RoomApplyReq;
import com.luohuo.flex.im.domain.vo.request.member.ApplyReq;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.luohuo.flex.im.domain.enums.NoticeStatusEnum.*;

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
	private UserSummaryCache userSummaryCache;
	private GroupMemberCache groupMemberCache;
	private RoomGroupCache roomGroupCache;
	private GroupMemberDao groupMemberDao;
	private FriendService friendService;
	private NoticeService noticeService;
	private CachePlusOps cachePlusOps;
	private RoomAppService roomAppService;
	private TransactionTemplate transactionTemplate;

    /**
     * 申请好友
     *
     * @param request 请求
     */
    @Override
	@RedissonLock(prefixKey = "friend:handlerApply", key = "#uid")
    public UserApply handlerApply(Long uid, FriendApplyReq request) {
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
			handlerApply(uid, new ApplyReq(friendApproving.getId(), ACCEPTED.getStatus()));
            return null;
        }
        // 申请入库
        UserApply newApply = FriendAdapter.buildFriendApply(uid, request);
        userApplyDao.save(newApply);

		noticeService.createNotice(
			RoomTypeEnum.FRIEND,
			NoticeTypeEnum.ADD_ME,
			uid,
			request.getTargetUid(),
			newApply.getId(),
			request.getTargetUid(),
			request.getMsg()
		);

		noticeService.createNotice(
			RoomTypeEnum.FRIEND,
			NoticeTypeEnum.FRIEND_APPLY,
			uid,
			uid,
			newApply.getId(),
			request.getTargetUid(),
			request.getMsg()
		);

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

		if(req.getType().equals(2) && !roomGroup.getAllowScanEnter()){
			return false;
		}

		List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
		if(memberUidList.contains(uid)){
			throw new BizException("你已经加入群聊");
		}

		// 获取到这个群的管理员的人的信息
		List<Long> groupAdminIds = roomService.getGroupUsers(roomGroup.getId(), true);
        SummeryInfoDTO userInfo = userSummaryCache.get(uid);
        String msg = StrUtil.format("用户{}申请加入群聊{}", userInfo.getName(), roomGroup.getName());
        if (req.getType().equals(2) && StrUtil.isBlank(req.getMsg())) {
            req.setMsg("扫码加入群聊");
        } else if (StrUtil.isBlank(req.getMsg())) {
            req.setMsg("申请加入群聊");
        }

		// 申请进入群聊
		Integer channel = req.getType().equals(2) ? 2 : 3;
		Long applyId = friendService.createUserApply(uid, roomGroup.getRoomId(), uid, msg, RoomTypeEnum.GROUP.getType(), channel);
		noticeService.createNotice(
				RoomTypeEnum.GROUP,
				NoticeTypeEnum.GROUP_APPLY,
				uid,
				uid,
				applyId,
				uid,
				roomGroup.getRoomId(),
                req.getMsg()
        );

		for (Long groupAdminId : groupAdminIds) {
			noticeService.createNotice(
				RoomTypeEnum.GROUP,
				NoticeTypeEnum.GROUP_APPLY,
				uid,
				groupAdminId,
				applyId,
				uid,
				roomGroup.getRoomId(),
                req.getMsg()
            );
		}
		return true;
	}

	@Override
	@RedissonLock(key = "#request.applyId")
	public void handlerApply(Long uid, ApplyReq request) {
		// 1. 校验邀请记录
		UserApply invite = userApplyDao.getById(request.getApplyId());
		if (invite == null) {
			throw new BizException("无效的邀请");
		}

		if (request.getState().equals(UNTREATED.getStatus())) {
			throw new BizException("无效的审批状态");
		}

		if (!invite.getStatus().equals(UNTREATED.getStatus())) {
			throw new BizException("无效的审批");
		}

		Notice notice = noticeService.getByApplyId(uid, invite.getId());
		if(ObjectUtil.isNull(notice)){
			throw new BizException("通知无法处理");
		}

		switch (NoticeStatusEnum.get(request.getState())){
			case REJECTED -> {
				userApplyDao.updateStatus(request.getApplyId(), NoticeStatusEnum.REJECTED);
				// 发送通知
				notice.setStatus(NoticeStatusEnum.REJECTED.getStatus());
				noticeService.updateNotice(notice);
			}
			case ACCEPTED -> {
				// 处理加好友
				invite.setStatus(request.getState());
				if(invite.getType().equals(RoomTypeEnum.FRIEND.getType())){
					AssertUtil.equal(invite.getStatus(), ACCEPTED.getStatus(), "已同意好友申请");
					// 同意申请
					AtomicReference<Long> atomicRoomId = new AtomicReference(0L);
					AtomicReference<Boolean> atomicIsFromTempSession = new AtomicReference(false);
					transactionTemplate.execute(e -> {
						userApplyDao.agree(request.getApplyId());
						UserFriend userFriend = userFriendDao.getByFriend(uid, invite.getUid());
						atomicIsFromTempSession.set(userFriend != null && userFriend.getIsTemp());
						// 如果是从临时会话升级，则修改会话状态；否则创建新会话
						if (atomicIsFromTempSession.get()) {
							userFriend.setIsTemp(false);
							userFriendDao.updateById(userFriend);
						} else {
							// 创建一个聊天房间
							RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, invite.getUid()));

							// 创建双方好友关系
							friendService.createFriend(roomFriend.getRoomId(), uid, invite.getUid());
							atomicRoomId.set(roomFriend.getRoomId());
						}
						// 更新邀请状态
						userApplyDao.updateById(invite);
						return true;
					});

					// 如果是从临时会话升级，则修改会话状态；否则创建新会话
					if (!atomicIsFromTempSession.get()) {
						// 添加双方好友主动关系、被动关系
						cachePlusOps.sAdd(FriendCacheKeyBuilder.userFriendsKey(uid), invite.getUid());
						cachePlusOps.sAdd(FriendCacheKeyBuilder.reverseFriendsKey(invite.getUid()), uid);
						cachePlusOps.sAdd(FriendCacheKeyBuilder.userFriendsKey(invite.getUid()), uid);
						cachePlusOps.sAdd(FriendCacheKeyBuilder.reverseFriendsKey(uid), invite.getUid());
						friendService.warmUpRoomMemberCache(Arrays.asList(atomicRoomId.get()));

						// 发送一条同意消息。。我们已经是好友了，开始聊天吧
						chatService.sendMsg(MessageAdapter.buildAgreeMsg(atomicRoomId.get(), true), uid);
					}

					// 通过好友申请
					notice.setStatus(NoticeStatusEnum.ACCEPTED.getStatus());
					noticeService.updateNotice(notice);

					// 通知请求方已处理好友申请
					SpringUtils.publishEvent(new UserApprovalEvent(this, RequestApprovalDto.builder().uid(uid).targetUid(invite.getUid()).build()));
				} else {
					// 处理加群, 如果是申请进群，那么用uid、否则是拉进群
					Long infoUid = invite.getApplyFor() ? invite.getUid() : invite.getTargetId();
					RoomGroup roomGroup = roomGroupCache.getByRoomId(invite.getRoomId());

					// 如果是申请进群，那么判断当前人员是否具备权限
					if(invite.getApplyFor()){
						GroupMember thisMember = groupMemberDao.getMemberByGroupId(roomGroup.getId(), uid);
						if (thisMember == null || !(thisMember.getRoleId().equals(GroupRoleEnum.LEADER.getType()) && !thisMember.getRoleId().equals(GroupRoleEnum.MANAGER.getType()))) {
							throw new BizException("无审批权限");
						}
					}

					Room room = roomCache.get(invite.getRoomId());
					GroupMember member = groupMemberDao.getMemberByGroupId(roomGroup.getId(), infoUid);
					if(ObjectUtil.isNotNull(member)){
						throw new BizException(StrUtil.format("{}已经在{}里", userSummaryCache.get(invite.getTargetId()).getName(), roomGroup.getName()));
					}

					transactionTemplate.execute(e -> {
						groupMemberDao.save(MemberAdapter.buildMemberAdd(roomGroup.getId(), infoUid));

						// 创建进群后的会话
						chatService.createContact(infoUid, roomGroup.getRoomId());

						// 更新邀请状态
						userApplyDao.updateById(invite);
						return true;
					});

					// 加群申请已同意
					notice.setStatus(NoticeStatusEnum.ACCEPTED.getStatus());
					noticeService.updateNotices(notice);

					// 3.3 写入缓存
					groupMemberCache.evictMemberList(invite.getRoomId());
					groupMemberCache.evictExceptMemberList(invite.getRoomId());
					CacheKey uKey = PresenceCacheKeyBuilder.userGroupsKey(infoUid);
					CacheKey gKey = PresenceCacheKeyBuilder.groupMembersKey(room.getId());
					CacheKey onlineGroupMembersKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(room.getId());
					cachePlusOps.sAdd(uKey, room.getId());
					cachePlusOps.sAdd(gKey, infoUid);
					roomAppService.asyncOnline(Arrays.asList(infoUid), room.getId(), true);

					SpringUtils.publishEvent(new GroupInviteMemberEvent(this, room.getId(), Arrays.asList(infoUid), invite.getUid(), invite.getApplyFor(), invite.getJoinChannel()));
                    SpringUtils.publishEvent(new GroupMemberAddEvent(this, room.getId(), Math.toIntExact(cachePlusOps.sCard(gKey)), Math.toIntExact(cachePlusOps.sCard(onlineGroupMembersKey)), Arrays.asList(infoUid), uid));
				}
			}
			case IGNORE -> {
				checkRecord(request);
				userApplyDao.updateStatus(request.getApplyId(), IGNORE);
				notice.setStatus(IGNORE.getStatus());
				noticeService.updateNotice(notice);
			}
		}
	}

    @Override
	@RedissonLock(prefixKey = "friend:deleteApprove", key = "#uid")
    public void deleteApprove(Long uid, ApplyReq request) {
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

    private UserApply checkRecord(ApplyReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), UNTREATED.getStatus(), "对方已是您的好友");
        return userApply;
    }
}
