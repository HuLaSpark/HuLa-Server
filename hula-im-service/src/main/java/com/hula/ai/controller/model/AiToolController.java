package com.hula.ai.controller.model;


import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.model.vo.tool.AiToolPageReqVO;
import com.hula.ai.controller.model.vo.tool.AiToolRespVO;
import com.hula.ai.controller.model.vo.tool.AiToolSaveReqVO;
import com.hula.ai.dal.model.AiToolDO;
import com.hula.ai.enums.CommonStatusEnum;
import com.hula.ai.service.model.AiToolService;
import com.hula.ai.utils.BeanUtils;
import com.hula.domain.vo.res.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hula.common.utils.CollectionUtils.convertList;


@Tag(name = "管理后台 - AI 工具")
@RestController
@RequestMapping("/ai/tool")
@Validated
public class AiToolController {

    @Resource
    private AiToolService toolService;

    @PostMapping("/create")
    @Operation(summary = "创建工具")
    public ApiResult<Long> createTool(@Valid @RequestBody AiToolSaveReqVO createReqVO) {
        return ApiResult.success(toolService.createTool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工具")
    public ApiResult<Boolean> updateTool(@Valid @RequestBody AiToolSaveReqVO updateReqVO) {
        toolService.updateTool(updateReqVO);
        return ApiResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工具")
    @Parameter(name = "id", description = "编号", required = true)
    public ApiResult<Boolean> deleteTool(@RequestParam("id") Long id) {
        toolService.deleteTool(id);
        return ApiResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工具")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public ApiResult<AiToolRespVO> getTool(@RequestParam("id") Long id) {
        AiToolDO tool = toolService.getTool(id);
        return ApiResult.success(BeanUtils.toBean(tool, AiToolRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得工具分页")
    public ApiResult<PageResult<AiToolRespVO>> getToolPage(@Valid AiToolPageReqVO pageReqVO) {
        PageResult<AiToolDO> pageResult = toolService.getToolPage(pageReqVO);
        return ApiResult.success(BeanUtils.toBean(pageResult, AiToolRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得工具列表")
    public ApiResult<List<AiToolRespVO>> getToolSimpleList() {
        List<AiToolDO> list = toolService.getToolListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return ApiResult.success(convertList(list, tool -> new AiToolRespVO()
                .setId(tool.getId()).setName(tool.getName())));
    }

}