package com.luohuo.flex.ai.controller.mindmap;


import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapGenerateReqVO;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapPageReqVO;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapRespVO;
import com.luohuo.flex.ai.dal.mindmap.AiMindMapDO;
import com.luohuo.flex.ai.service.mindmap.AiMindMapService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.luohuo.basic.base.R.success;


@Tag(name = "管理后台 - AI 思维导图")
@RestController
@RequestMapping("/mind-map")
public class AiMindMapController {

    @Resource
    private AiMindMapService mindMapService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "导图生成（流式）", description = "流式返回，响应较快")
    public Flux<R<String>> generateMindMap(@RequestBody @Valid AiMindMapGenerateReqVO generateReqVO) {
        return mindMapService.generateMindMap(generateReqVO, ContextUtil.getUid());
    }

    // ================ 导图管理 ================

    @DeleteMapping("/delete")
    @Operation(summary = "删除思维导图")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteMindMap(@RequestParam("id") Long id) {
        mindMapService.deleteMindMap(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得思维导图分页")
    public R<PageResult<AiMindMapRespVO>> getMindMapPage(@Valid AiMindMapPageReqVO pageReqVO) {
        PageResult<AiMindMapDO> pageResult = mindMapService.getMindMapPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiMindMapRespVO.class));
    }

}
