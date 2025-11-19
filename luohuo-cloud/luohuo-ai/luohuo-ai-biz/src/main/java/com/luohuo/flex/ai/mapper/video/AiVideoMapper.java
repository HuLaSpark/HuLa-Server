package com.luohuo.flex.ai.mapper.video;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.video.vo.AiVideoPageReqVO;
import com.luohuo.flex.ai.dal.video.AiVideoDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiVideoMapper extends BaseMapperX<AiVideoDO> {

    default PageResult<AiVideoDO> selectPage(AiVideoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiVideoDO>()
                .eqIfPresent(AiVideoDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiVideoDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(AiVideoDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AiVideoDO::getPublicStatus, reqVO.getPublicStatus())
                .betweenIfPresent(AiVideoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AiVideoDO::getId));
    }

    default PageResult<AiVideoDO> selectPageMy(Long userId, AiVideoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiVideoDO>()
                .likeIfPresent(AiVideoDO::getPrompt, reqVO.getPrompt())
                .eq(Boolean.TRUE.equals(reqVO.getPublicStatus()), AiVideoDO::getPublicStatus, reqVO.getPublicStatus())
                .orderByDesc(AiVideoDO::getId));
    }

    default List<AiVideoDO> selectListByStatusAndPlatform(Integer status, String platform) {
        return selectList(AiVideoDO::getStatus, status,
                AiVideoDO::getPlatform, platform);
    }

    default List<AiVideoDO> selectListByStatusWithTaskId(Integer status) {
        return selectList(new LambdaQueryWrapperX<AiVideoDO>()
                .eq(AiVideoDO::getStatus, status)
                .isNotNull(AiVideoDO::getTaskId)
                .orderByAsc(AiVideoDO::getCreateTime));
    }

    default AiVideoDO selectByTaskId(String taskId) {
        return selectOne(AiVideoDO::getTaskId, taskId);
    }

}
