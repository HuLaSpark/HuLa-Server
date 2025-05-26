package com.hula.ai.controller.workflow;


import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.workflow.vo.AiWorkflowPageReqVO;
import com.hula.ai.controller.workflow.vo.AiWorkflowRespVO;
import com.hula.ai.controller.workflow.vo.AiWorkflowSaveReqVO;
import com.hula.ai.controller.workflow.vo.AiWorkflowTestReqVO;
import com.hula.ai.dal.workflow.AiWorkflowDO;
import com.hula.ai.service.workflow.AiWorkflowService;
import com.hula.ai.utils.BeanUtils;
import com.hula.domain.vo.res.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.hula.ai.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - AI 工作流")
@RestController
@RequestMapping("/ai/workflow")
@Slf4j
public class AiWorkflowController {

    @Resource
    private AiWorkflowService workflowService;

    @PostMapping("/create")
    @Operation(summary = "创建 AI 工作流")
    public ApiResult<Long> createWorkflow(@Valid @RequestBody AiWorkflowSaveReqVO createReqVO) {
        return success(workflowService.createWorkflow(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 AI 工作流")
    public ApiResult<Boolean> updateWorkflow(@Valid @RequestBody AiWorkflowSaveReqVO updateReqVO) {
        workflowService.updateWorkflow(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 AI 工作流")
    @Parameter(name = "id", description = "编号", required = true)
    public ApiResult<Boolean> deleteWorkflow(@RequestParam("id") Long id) {
        workflowService.deleteWorkflow(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 AI 工作流")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public ApiResult<AiWorkflowRespVO> getWorkflow(@RequestParam("id") Long id) {
        AiWorkflowDO workflow = workflowService.getWorkflow(id);
        return success(BeanUtils.toBean(workflow, AiWorkflowRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得 AI 工作流分页")
    public ApiResult<PageResult<AiWorkflowRespVO>> getWorkflowPage(@Valid AiWorkflowPageReqVO pageReqVO) {
        PageResult<AiWorkflowDO> pageResult = workflowService.getWorkflowPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiWorkflowRespVO.class));
    }

    @PostMapping("/test")
    @Operation(summary = "测试 AI 工作流")
    public ApiResult<Object> testWorkflow(@Valid @RequestBody AiWorkflowTestReqVO testReqVO) {
        return success(workflowService.testWorkflow(testReqVO));
    }

}
