package com.hula.ai.controller.mindmap;


import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.mindmap.vo.AiMindMapGenerateReqVO;
import com.hula.ai.controller.mindmap.vo.AiMindMapPageReqVO;
import com.hula.ai.controller.mindmap.vo.AiMindMapRespVO;
import com.hula.ai.dal.mindmap.AiMindMapDO;
import com.hula.ai.service.mindmap.AiMindMapService;
import com.hula.ai.utils.BeanUtils;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static com.hula.ai.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - AI 思维导图")
@RestController
@RequestMapping("/ai/mind-map")
public class AiMindMapController {

    @Resource
    private AiMindMapService mindMapService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "导图生成（流式）", description = "流式返回，响应较快")
    public Flux<ApiResult<String>> generateMindMap(@RequestBody @Valid AiMindMapGenerateReqVO generateReqVO) {
        return mindMapService.generateMindMap(generateReqVO, RequestHolder.get().getUid());
    }

    // ================ 导图管理 ================

    @DeleteMapping("/delete")
    @Operation(summary = "删除思维导图")
    @Parameter(name = "id", description = "编号", required = true)
    public ApiResult<Boolean> deleteMindMap(@RequestParam("id") Long id) {
        mindMapService.deleteMindMap(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得思维导图分页")
    public ApiResult<PageResult<AiMindMapRespVO>> getMindMapPage(@Valid AiMindMapPageReqVO pageReqVO) {
        PageResult<AiMindMapDO> pageResult = mindMapService.getMindMapPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiMindMapRespVO.class));
    }

}
