package com.hula.core.user.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.user.domain.entity.UserApply;
import com.hula.core.user.domain.enums.ApplyDeletedEnum;
import com.hula.core.user.domain.enums.ApplyStatusEnum;
import com.hula.core.user.domain.enums.ApplyTypeEnum;
import com.hula.core.user.mapper.UserApplyMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.hula.core.user.domain.enums.ApplyReadStatusEnum.READ;
import static com.hula.core.user.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.hula.core.user.domain.enums.ApplyStatusEnum.AGREE;
import static com.hula.core.user.domain.enums.ApplyStatusEnum.REJECT;

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
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getCode())
                .notIn(initiator,UserApply::getDeleted,ApplyDeletedEnum.applyDeleted())
                .notIn(!initiator,UserApply::getDeleted,ApplyDeletedEnum.targetDeleted())
                .one();
    }

    public Integer getUnReadCount(Long targetId) {
        return Math.toIntExact(lambdaQuery().eq(UserApply::getTargetId, targetId)
                .eq(UserApply::getReadStatus, UNREAD.getCode())
                .eq(UserApply::getDeleted,ApplyDeletedEnum.NORMAL.getCode())
                .count());
    }

    public IPage<UserApply> friendApplyPage(Long uid, Page<UserApply> page) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getCode())
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

    public void updateStatus(Long applyId,ApplyStatusEnum statusEnum) {
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
}
