package com.hula.ai.service.workflow;

import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.workflow.vo.AiWorkflowPageReqVO;
import com.hula.ai.controller.workflow.vo.AiWorkflowSaveReqVO;
import com.hula.ai.controller.workflow.vo.AiWorkflowTestReqVO;
import com.hula.ai.dal.workflow.AiWorkflowDO;
import jakarta.validation.Valid;

/**
 * AI 工作流 Service 接口
 *
 */
public interface AiWorkflowService {

    /**
     * 创建 AI 工作流
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWorkflow(@Valid AiWorkflowSaveReqVO createReqVO);

    /**
     * 更新 AI 工作流
     *
     * @param updateReqVO 更新信息
     */
    void updateWorkflow(@Valid AiWorkflowSaveReqVO updateReqVO);

    /**
     * 删除 AI 工作流
     *
     * @param id 编号
     */
    void deleteWorkflow(Long id);

    /**
     * 获得 AI 工作流
     *
     * @param id 编号
     * @return AI 工作流
     */
    AiWorkflowDO getWorkflow(Long id);

    /**
     * 获得 AI 工作流分页
     *
     * @param pageReqVO 分页查询
     * @return AI 工作流分页
     */
    PageResult<AiWorkflowDO> getWorkflowPage(AiWorkflowPageReqVO pageReqVO);

    /**
     * 测试 AI 工作流
     *
     * @param testReqVO 测试数据
     */
    Object testWorkflow(AiWorkflowTestReqVO testReqVO);

}
