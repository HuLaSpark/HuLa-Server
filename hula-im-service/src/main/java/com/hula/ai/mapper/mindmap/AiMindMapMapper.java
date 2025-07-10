package com.hula.ai.mapper.mindmap;

import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.mindmap.vo.AiMindMapPageReqVO;
import com.hula.ai.dal.mindmap.AiMindMapDO;
import com.hula.ai.mapper.BaseMapperX;
import com.hula.ai.mapper.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 思维导图 Mapper
 *
 * @author xiaoxin
 */
@Mapper
public interface AiMindMapMapper extends BaseMapperX<AiMindMapDO> {

    default PageResult<AiMindMapDO> selectPage(AiMindMapPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMindMapDO>()
                .eqIfPresent(AiMindMapDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiMindMapDO::getPrompt, reqVO.getPrompt())
                .betweenIfPresent(AiMindMapDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AiMindMapDO::getId));
    }

}
