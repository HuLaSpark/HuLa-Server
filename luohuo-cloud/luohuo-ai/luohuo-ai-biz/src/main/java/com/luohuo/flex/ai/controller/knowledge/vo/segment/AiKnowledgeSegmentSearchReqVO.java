package com.luohuo.flex.ai.controller.knowledge.vo.segment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI 知识库段落搜索 Request VO")
@Data
public class AiKnowledgeSegmentSearchReqVO {

    @Schema(description = "知识库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "知识库编号不能为空")
    private Long knowledgeId;

    @Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "如何使用这个产品")
    @NotEmpty(message = "内容不能为空")
    private String content;

    @Schema(description = "最大返回数量", example = "5")
    private Integer topK;

    @Schema(description = "相似度阈值", example = "0.7")
    private Double similarityThreshold;

}
