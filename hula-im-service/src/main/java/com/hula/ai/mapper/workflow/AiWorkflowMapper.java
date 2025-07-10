package com.hula.ai.mapper.workflow;


import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.workflow.vo.AiWorkflowPageReqVO;
import com.hula.ai.dal.workflow.AiWorkflowDO;
import com.hula.ai.mapper.BaseMapperX;
import com.hula.ai.mapper.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 工作流 Mapper
 *
 */
@Mapper
public interface AiWorkflowMapper extends BaseMapperX<AiWorkflowDO> {

    default AiWorkflowDO selectByCode(String code) {
        return selectOne(AiWorkflowDO::getCode, code);
    }

    default PageResult<AiWorkflowDO> selectPage(AiWorkflowPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<AiWorkflowDO>()
                .eqIfPresent(AiWorkflowDO::getStatus, pageReqVO.getStatus())
                .likeIfPresent(AiWorkflowDO::getName, pageReqVO.getName())
                .likeIfPresent(AiWorkflowDO::getCode, pageReqVO.getCode())
                .betweenIfPresent(AiWorkflowDO::getCreateTime, pageReqVO.getCreateTime()));
    }

}
