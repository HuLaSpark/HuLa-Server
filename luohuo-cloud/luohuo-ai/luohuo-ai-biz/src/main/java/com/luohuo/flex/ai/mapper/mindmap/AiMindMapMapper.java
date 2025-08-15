package com.luohuo.flex.ai.mapper.mindmap;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapPageReqVO;
import com.luohuo.flex.ai.dal.mindmap.AiMindMapDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import org.springframework.stereotype.Repository;

/**
 * AI 思维导图 Mapper
 *
 * @author xiaoxin
 */
@Repository
public interface AiMindMapMapper extends BaseMapperX<AiMindMapDO> {

    default PageResult<AiMindMapDO> selectPage(AiMindMapPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMindMapDO>()
                .eqIfPresent(AiMindMapDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiMindMapDO::getPrompt, reqVO.getPrompt())
                .betweenIfPresent(AiMindMapDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AiMindMapDO::getId));
    }

}
