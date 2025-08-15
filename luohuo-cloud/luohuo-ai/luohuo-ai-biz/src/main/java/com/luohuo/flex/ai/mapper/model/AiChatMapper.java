package com.luohuo.flex.ai.mapper.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelPageReqVO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import com.luohuo.flex.ai.mapper.QueryWrapperX;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * API 模型 Mapper
 */
@Repository
public interface AiChatMapper extends BaseMapperX<AiModelDO> {

    default AiModelDO selectFirstByStatus(Integer type, Integer status) {
        return selectOne(new QueryWrapperX<AiModelDO>()
                .eq("type", type)
                .eq("status", status)
                .limitN(1)
                .orderByAsc("sort"));
    }

    default PageResult<AiModelDO> selectPage(AiModelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiModelDO>()
                .likeIfPresent(AiModelDO::getName, reqVO.getName())
                .eqIfPresent(AiModelDO::getModel, reqVO.getModel())
                .eqIfPresent(AiModelDO::getPlatform, reqVO.getPlatform())
                .orderByAsc(AiModelDO::getSort));
    }

    default List<AiModelDO> selectListByStatusAndType(Integer status, Integer type,
                                                      @Nullable String platform) {
        return selectList(new LambdaQueryWrapperX<AiModelDO>()
                .eq(AiModelDO::getStatus, status)
                .eq(AiModelDO::getType, type)
                .eqIfPresent(AiModelDO::getPlatform, platform)
                .orderByAsc(AiModelDO::getSort));
    }

}
