package com.luohuo.flex.ai.controller.knowledge;

import cn.hutool.core.collection.CollUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentPageReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentProcessRespVO;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentRespVO;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentSaveReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentSearchReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentSearchRespVO;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentUpdateStatusReqVO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeDocumentDO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeSegmentDO;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeDocumentService;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeSegmentService;
import com.luohuo.flex.ai.service.knowledge.bo.AiKnowledgeSegmentSearchReqBO;
import com.luohuo.flex.ai.service.knowledge.bo.AiKnowledgeSegmentSearchRespBO;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.flex.ai.utils.MapUtils;
import com.luohuo.basic.base.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.luohuo.basic.base.R.success;
import static com.luohuo.basic.utils.collection.CollectionUtils.convertSet;


@Tag(name = "管理后台 - AI 知识库段落")
@RestController
@RequestMapping("/knowledge/segment")
@Validated
public class AiKnowledgeSegmentController {

    @Resource
    private AiKnowledgeSegmentService segmentService;
    @Resource
    private AiKnowledgeDocumentService documentService;

    @GetMapping("/get")
    @Operation(summary = "获取段落详情")
    @Parameter(name = "id", description = "段落编号", required = true, example = "1024")
    public R<AiKnowledgeSegmentRespVO> getKnowledgeSegment(@RequestParam("id") Long id) {
        AiKnowledgeSegmentDO segment = segmentService.getKnowledgeSegment(id);
        return success(BeanUtils.toBean(segment, AiKnowledgeSegmentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取段落分页")
    public R<PageResult<AiKnowledgeSegmentRespVO>> getKnowledgeSegmentPage(
            @Valid AiKnowledgeSegmentPageReqVO pageReqVO) {
        PageResult<AiKnowledgeSegmentDO> pageResult = segmentService.getKnowledgeSegmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiKnowledgeSegmentRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建段落")
    public R<Long> createKnowledgeSegment(@Valid @RequestBody AiKnowledgeSegmentSaveReqVO createReqVO) {
        return success(segmentService.createKnowledgeSegment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新段落内容")
    public R<Boolean> updateKnowledgeSegment(@Valid @RequestBody AiKnowledgeSegmentSaveReqVO reqVO) {
        segmentService.updateKnowledgeSegment(reqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "启禁用段落内容")
    public R<Boolean> updateKnowledgeSegmentStatus(
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
    public R<List<AiKnowledgeSegmentRespVO>> splitContent(
            @RequestParam("url") @URL String url,
            @RequestParam(value = "segmentMaxTokens") Integer segmentMaxTokens) {
        List<AiKnowledgeSegmentDO> segments = segmentService.splitContent(url, segmentMaxTokens);
        return success(BeanUtils.toBean(segments, AiKnowledgeSegmentRespVO.class));
    }

    @GetMapping("/get-process-list")
    @Operation(summary = "获取文档处理列表")
    @Parameter(name = "documentIds", description = "文档编号列表", required = true, example = "1,2,3")
    public R<List<AiKnowledgeSegmentProcessRespVO>> getKnowledgeSegmentProcessList(
            @RequestParam("documentIds") List<Long> documentIds) {
        List<AiKnowledgeSegmentProcessRespVO> list = segmentService.getKnowledgeSegmentProcessList(documentIds);
        return success(list);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索段落内容")
    public R<List<AiKnowledgeSegmentSearchRespVO>> searchKnowledgeSegment(
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
