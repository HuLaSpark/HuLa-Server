package com.hula.ai.controller.knowledge;

import cn.hutool.core.collection.CollUtil;
import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.knowledge.vo.segment.*;
import com.hula.ai.dal.knowledge.AiKnowledgeDocumentDO;
import com.hula.ai.dal.knowledge.AiKnowledgeSegmentDO;
import com.hula.ai.service.knowledge.AiKnowledgeDocumentService;
import com.hula.ai.service.knowledge.AiKnowledgeSegmentService;
import com.hula.ai.service.knowledge.bo.AiKnowledgeSegmentSearchReqBO;
import com.hula.ai.service.knowledge.bo.AiKnowledgeSegmentSearchRespBO;
import com.hula.ai.utils.BeanUtils;
import com.hula.ai.utils.MapUtils;
import com.hula.domain.vo.res.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hula.ai.common.pojo.CommonResult.success;
import static com.hula.common.utils.CollectionUtils.convertSet;


@Tag(name = "管理后台 - AI 知识库段落")
@RestController
@RequestMapping("/ai/knowledge/segment")
@Validated
public class AiKnowledgeSegmentController {

    @Resource
    private AiKnowledgeSegmentService segmentService;
    @Resource
    private AiKnowledgeDocumentService documentService;

    @GetMapping("/get")
    @Operation(summary = "获取段落详情")
    @Parameter(name = "id", description = "段落编号", required = true, example = "1024")
    public ApiResult<AiKnowledgeSegmentRespVO> getKnowledgeSegment(@RequestParam("id") Long id) {
        AiKnowledgeSegmentDO segment = segmentService.getKnowledgeSegment(id);
        return success(BeanUtils.toBean(segment, AiKnowledgeSegmentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取段落分页")
    public ApiResult<PageResult<AiKnowledgeSegmentRespVO>> getKnowledgeSegmentPage(
            @Valid AiKnowledgeSegmentPageReqVO pageReqVO) {
        PageResult<AiKnowledgeSegmentDO> pageResult = segmentService.getKnowledgeSegmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiKnowledgeSegmentRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建段落")
    public ApiResult<Long> createKnowledgeSegment(@Valid @RequestBody AiKnowledgeSegmentSaveReqVO createReqVO) {
        return success(segmentService.createKnowledgeSegment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新段落内容")
    public ApiResult<Boolean> updateKnowledgeSegment(@Valid @RequestBody AiKnowledgeSegmentSaveReqVO reqVO) {
        segmentService.updateKnowledgeSegment(reqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "启禁用段落内容")
    public ApiResult<Boolean> updateKnowledgeSegmentStatus(
            @Valid @RequestBody AiKnowledgeSegmentUpdateStatusReqVO reqVO) {
        segmentService.updateKnowledgeSegmentStatus(reqVO);
        return success(true);
    }

    @GetMapping("/split")
    @Operation(summary = "切片内容")
    @Parameters({
            @Parameter(name = "url", description = "文档 URL", required = true),
            @Parameter(name = "segmentMaxTokens", description = "分段的最大 Token 数", required = true)
    })
    public ApiResult<List<AiKnowledgeSegmentRespVO>> splitContent(
            @RequestParam("url") @URL String url,
            @RequestParam(value = "segmentMaxTokens") Integer segmentMaxTokens) {
        List<AiKnowledgeSegmentDO> segments = segmentService.splitContent(url, segmentMaxTokens);
        return success(BeanUtils.toBean(segments, AiKnowledgeSegmentRespVO.class));
    }

    @GetMapping("/get-process-list")
    @Operation(summary = "获取文档处理列表")
    @Parameter(name = "documentIds", description = "文档编号列表", required = true, example = "1,2,3")
    public ApiResult<List<AiKnowledgeSegmentProcessRespVO>> getKnowledgeSegmentProcessList(
            @RequestParam("documentIds") List<Long> documentIds) {
        List<AiKnowledgeSegmentProcessRespVO> list = segmentService.getKnowledgeSegmentProcessList(documentIds);
        return success(list);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索段落内容")
    public ApiResult<List<AiKnowledgeSegmentSearchRespVO>> searchKnowledgeSegment(
            @Valid AiKnowledgeSegmentSearchReqVO reqVO) {
        // 1. 搜索段落
        List<AiKnowledgeSegmentSearchRespBO> segments = segmentService
                .searchKnowledgeSegment(BeanUtils.toBean(reqVO, AiKnowledgeSegmentSearchReqBO.class));
        if (CollUtil.isEmpty(segments)) {
            return success(Collections.emptyList());
        }

        // 2. 拼接 VO
        Map<Long, AiKnowledgeDocumentDO> documentMap = documentService.getKnowledgeDocumentMap(convertSet(
                segments, AiKnowledgeSegmentSearchRespBO::getDocumentId));
        return success(BeanUtils.toBean(segments, AiKnowledgeSegmentSearchRespVO.class,
                segment -> MapUtils.findAndThen(documentMap, segment.getDocumentId(),
                        document -> segment.setDocumentName(document.getName()))));
    }

}
