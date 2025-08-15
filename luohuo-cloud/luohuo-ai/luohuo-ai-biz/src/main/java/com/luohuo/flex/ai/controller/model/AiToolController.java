package com.luohuo.flex.ai.controller.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.tool.AiToolPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.tool.AiToolRespVO;
import com.luohuo.flex.ai.controller.model.vo.tool.AiToolSaveReqVO;
import com.luohuo.flex.ai.dal.model.AiToolDO;
import com.luohuo.flex.ai.enums.CommonStatusEnum;
import com.luohuo.flex.ai.service.model.AiToolService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.luohuo.basic.utils.collection.CollectionUtils.convertList;


@Tag(name = "管理后台 - AI 工具")
@RestController
@RequestMapping("/tool")
@Validated
public class AiToolController {

    @Resource
    private AiToolService toolService;

    @PostMapping("/create")
    @Operation(summary = "创建工具")
    public R<Long> createTool(@Valid @RequestBody AiToolSaveReqVO createReqVO) {
        return R.success(toolService.createTool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工具")
    public R<Boolean> updateTool(@Valid @RequestBody AiToolSaveReqVO updateReqVO) {
        toolService.updateTool(updateReqVO);
        return R.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工具")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteTool(@RequestParam("id") Long id) {
        toolService.deleteTool(id);
        return R.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工具")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<AiToolRespVO> getTool(@RequestParam("id") Long id) {
        AiToolDO tool = toolService.getTool(id);
        return R.success(BeanUtils.toBean(tool, AiToolRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得工具分页")
    public R<PageResult<AiToolRespVO>> getToolPage(@Valid AiToolPageReqVO pageReqVO) {
        PageResult<AiToolDO> pageResult = toolService.getToolPage(pageReqVO);
        return R.success(BeanUtils.toBean(pageResult, AiToolRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得工具列表")
    public R<List<AiToolRespVO>> getToolSimpleList() {
        List<AiToolDO> list = toolService.getToolListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return R.success(convertList(list, tool -> new AiToolRespVO()
                .setId(tool.getId()).setName(tool.getName())));
    }

}