package com.hula.ai.controller.knowledge;

import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.knowledge.vo.document.*;
import com.hula.ai.controller.knowledge.vo.knowledge.AiKnowledgeDocumentCreateReqVO;
import com.hula.ai.dal.knowledge.AiKnowledgeDocumentDO;
import com.hula.ai.service.knowledge.AiKnowledgeDocumentService;
import com.hula.ai.utils.BeanUtils;
import com.hula.domain.vo.res.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hula.ai.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - AI 知识库文档")
@RestController
@RequestMapping("/ai/knowledge/document")
@Validated
public class AiKnowledgeDocumentController {

    @Resource
    private AiKnowledgeDocumentService documentService;

    @GetMapping("/page")
    @Operation(summary = "获取文档分页")
    public ApiResult<PageResult<AiKnowledgeDocumentRespVO>> getKnowledgeDocumentPage(
            @Valid AiKnowledgeDocumentPageReqVO pageReqVO) {
        PageResult<AiKnowledgeDocumentDO> pageResult = documentService.getKnowledgeDocumentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiKnowledgeDocumentRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取文档详情")
    public ApiResult<AiKnowledgeDocumentRespVO> getKnowledgeDocument(@RequestParam("id") Long id) {
        AiKnowledgeDocumentDO document = documentService.getKnowledgeDocument(id);
        return success(BeanUtils.toBean(document, AiKnowledgeDocumentRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "新建文档（单个）")
    public ApiResult<Long> createKnowledgeDocument(@RequestBody @Valid AiKnowledgeDocumentCreateReqVO reqVO) {
        Long id = documentService.createKnowledgeDocument(reqVO);
        return success(id);
    }

    @PostMapping("/create-list")
    @Operation(summary = "新建文档（多个）")
    public ApiResult<List<Long>> createKnowledgeDocumentList(
            @RequestBody @Valid AiKnowledgeDocumentCreateListReqVO reqVO) {
        List<Long> ids = documentService.createKnowledgeDocumentList(reqVO);
        return success(ids);
    }

    @PutMapping("/update")
    @Operation(summary = "更新文档")
    public ApiResult<Boolean> updateKnowledgeDocument(@Valid @RequestBody AiKnowledgeDocumentUpdateReqVO reqVO) {
        documentService.updateKnowledgeDocument(reqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新文档状态")
    public ApiResult<Boolean> updateKnowledgeDocumentStatus(
            @Valid @RequestBody AiKnowledgeDocumentUpdateStatusReqVO reqVO) {
        documentService.updateKnowledgeDocumentStatus(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文档")
    public ApiResult<Boolean> deleteKnowledgeDocument(@RequestParam("id") Long id) {
        documentService.deleteKnowledgeDocument(id);
        return success(true);
    }

}
