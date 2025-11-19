package com.luohuo.flex.ai.controller.knowledge;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.knowledge.vo.document.AiKnowledgeDocumentCreateListReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.document.AiKnowledgeDocumentPageReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.document.AiKnowledgeDocumentRespVO;
import com.luohuo.flex.ai.controller.knowledge.vo.document.AiKnowledgeDocumentUpdateReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.document.AiKnowledgeDocumentUpdateStatusReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.knowledge.AiKnowledgeDocumentCreateReqVO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeDocumentDO;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeDocumentService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.luohuo.basic.base.R.success;


@Tag(name = "管理后台 - AI 知识库文档")
@RestController
@RequestMapping("/knowledge/document")
@Validated
public class AiKnowledgeDocumentController {

    @Resource
    private AiKnowledgeDocumentService documentService;

    @GetMapping("/page")
    @Operation(summary = "获取文档分页")
    public R<PageResult<AiKnowledgeDocumentRespVO>> getKnowledgeDocumentPage(
            @Valid AiKnowledgeDocumentPageReqVO pageReqVO) {
        PageResult<AiKnowledgeDocumentDO> pageResult = documentService.getKnowledgeDocumentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiKnowledgeDocumentRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取文档详情")
    public R<AiKnowledgeDocumentRespVO> getKnowledgeDocument(@RequestParam("id") Long id) {
        AiKnowledgeDocumentDO document = documentService.getKnowledgeDocument(id);
        return success(BeanUtils.toBean(document, AiKnowledgeDocumentRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "新建文档（单个）")
    public R<Long> createKnowledgeDocument(@RequestBody @Valid AiKnowledgeDocumentCreateReqVO reqVO) {
        Long id = documentService.createKnowledgeDocument(reqVO);
        return success(id);
    }

    @PostMapping("/create-list")
    @Operation(summary = "新建文档（多个）")
    public R<List<Long>> createKnowledgeDocumentList(
            @RequestBody @Valid AiKnowledgeDocumentCreateListReqVO reqVO) {
        List<Long> ids = documentService.createKnowledgeDocumentList(reqVO);
        return success(ids);
    }

    @PutMapping("/update")
    @Operation(summary = "更新文档")
    public R<Boolean> updateKnowledgeDocument(@Valid @RequestBody AiKnowledgeDocumentUpdateReqVO reqVO) {
        documentService.updateKnowledgeDocument(reqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新文档状态")
    public R<Boolean> updateKnowledgeDocumentStatus(
            @Valid @RequestBody AiKnowledgeDocumentUpdateStatusReqVO reqVO) {
        documentService.updateKnowledgeDocumentStatus(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文档")
    public R<Boolean> deleteKnowledgeDocument(@RequestParam("id") Long id) {
        documentService.deleteKnowledgeDocument(id);
        return success(true);
    }

}
