package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.enums.ApplyDeletedEnum;
import com.luohuo.flex.im.domain.enums.ApplyStatusEnum;
import com.luohuo.flex.im.core.user.mapper.UserApplyMapper;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendUnreadDto;
import com.luohuo.flex.model.entity.ws.WSFriendApply;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum.READ;
import static com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.luohuo.flex.im.domain.enums.ApplyStatusEnum.AGREE;

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
	 * 获取用户进群审批列表
	 */
	public CursorPageBaseResp<UserApply> getApplyPage(Long uid, Integer type, CursorPageBaseReq request) {
		return CursorUtils.getCursorPageByMysql(this, request, wrapper -> wrapper.eq(UserApply::getTargetId, uid).eq(UserApply::getType, type).eq(UserApply::getApplyFor, true), UserApply::getCreateTime);
	}

    /**
     *
     * @param uid       uid
     * @param targetUid 目标 UID
     * @param initiator 方法调用方是否是申请记录发起方
     * @return {@link UserApply }
     */
    public UserApply getFriendApproving(Long uid, Long targetUid,boolean initiator) {
        return lambdaQuery().eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetUid)
                .eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVAL.getCode())
                .eq(UserApply::getType, RoomTypeEnum.FRIEND.getType())
                .notIn(initiator,UserApply::getDeleted,ApplyDeletedEnum.applyDeleted())
                .notIn(!initiator,UserApply::getDeleted,ApplyDeletedEnum.targetDeleted())
                .one();
    }

	/**
	 * 返回好友、群聊申请的未读数量
	 * @param targetId
	 * @return
	 */
    public WSFriendApply getUnReadCount(Long uid, Long targetId) {
		List<FriendUnreadDto> unReadCountByTypeMap = baseMapper.getUnReadCountByType(targetId, UNREAD.getCode(), ApplyDeletedEnum.NORMAL.getCode());
		WSFriendApply wsFriendApply = new WSFriendApply();
		wsFriendApply.setUid(uid);

		// 构造需要的数据
		for (FriendUnreadDto friendUnreadDto : unReadCountByTypeMap) {
			if(friendUnreadDto.getType().equals(RoomTypeEnum.FRIEND.getType())){
				wsFriendApply.setUnReadCount4Friend(friendUnreadDto.getCount());
			} else {
				wsFriendApply.setUnReadCount4Group(friendUnreadDto.getCount());
			}
		}
		return wsFriendApply;
    }

    public IPage<UserApply> friendApplyPage(Long uid, Page<UserApply> page) {
        return lambdaQuery()
				.and(w -> w.eq(UserApply::getTargetId, uid).or().eq(UserApply::getUid, uid))
                .eq(UserApply::getDeleted,false)
                .orderByDesc(UserApply::getCreateTime)
                .page(page);
    }

    public void readApples(Long uid, List<Long> applyIds) {
        lambdaUpdate()
                .set(UserApply::getReadStatus, READ.getCode())
                .eq(UserApply::getReadStatus, UNREAD.getCode())
                .in(UserApply::getId, applyIds)
                .eq(UserApply::getTargetId, uid)
                .update();
    }

    public void agree(Long applyId) {
        lambdaUpdate()
                .eq(UserApply::getId, applyId)
                .set(UserApply::getStatus, AGREE.getCode())
                .set(UserApply::getUpdateTime, LocalDateTime.now())
                .update();
    }

    public void updateStatus(Long applyId, ApplyStatusEnum statusEnum) {
        lambdaUpdate().set(UserApply::getStatus, statusEnum.getCode())
                .set(UserApply::getUpdateTime, LocalDateTime.now())
                .eq(UserApply::getId,applyId)
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
				.eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVAL.getCode())
				.in(UserApply::getTargetId, uidList)
				.list().stream().map(UserApply::getTargetId).collect(Collectors.toList());
	}
}
