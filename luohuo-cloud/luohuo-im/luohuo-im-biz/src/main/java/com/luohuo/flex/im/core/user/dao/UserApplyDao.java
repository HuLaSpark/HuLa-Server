package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.enums.ApplyDeletedEnum;
import com.luohuo.flex.im.domain.enums.NoticeStatusEnum;
import com.luohuo.flex.im.core.user.mapper.UserApplyMapper;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.luohuo.flex.im.domain.enums.NoticeStatusEnum.ACCEPTED;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {

	/**
	 * @param uid       uid
	 * @param targetUid 目标 UID
	 * @param initiator 方法调用方是否是申请记录发起方
	 * @return {@link UserApply }
	 */
	public UserApply getFriendApproving(Long uid, Long targetUid, boolean initiator) {
		return lambdaQuery().eq(UserApply::getUid, uid)
				.eq(UserApply::getTargetId, targetUid)
				.eq(UserApply::getStatus, NoticeStatusEnum.UNTREATED.getStatus())
				.eq(UserApply::getType, RoomTypeEnum.FRIEND.getType())
				.notIn(initiator, UserApply::getDeleted, ApplyDeletedEnum.applyDeleted())
				.notIn(!initiator, UserApply::getDeleted, ApplyDeletedEnum.targetDeleted())
				.one();
	}

	public void agree(Long applyId) {
		lambdaUpdate()
				.eq(UserApply::getId, applyId)
				.set(UserApply::getStatus, ACCEPTED.getStatus())
				.set(UserApply::getUpdateTime, LocalDateTime.now())
				.update();
	}

	public void updateStatus(Long applyId, NoticeStatusEnum statusEnum) {
		lambdaUpdate().set(UserApply::getStatus, statusEnum.getStatus())
				.set(UserApply::getUpdateTime, LocalDateTime.now())
				.eq(UserApply::getId, applyId)
				.update();
	}

	public void deleteApprove(Long applyId, ApplyDeletedEnum deletedEnum) {
		lambdaUpdate().set(UserApply::getDeleted, deletedEnum.getCode())
				.set(UserApply::getUpdateTime, LocalDateTime.now())
				.eq(UserApply::getId, applyId)
				.update();
	}

	public List<Long> getExistingUsers(Long roomId, HashSet<Long> uidList) {
		return lambdaQuery()
				.eq(UserApply::getRoomId, roomId)
				.eq(UserApply::getStatus, NoticeStatusEnum.UNTREATED.getStatus())
				.in(UserApply::getTargetId, uidList)
				.list().stream().map(UserApply::getTargetId).collect(Collectors.toList());
	}
}
