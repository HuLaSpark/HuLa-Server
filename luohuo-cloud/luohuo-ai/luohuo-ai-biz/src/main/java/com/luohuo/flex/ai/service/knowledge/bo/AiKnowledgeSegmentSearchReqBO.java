package com.luohuo.flex.ai.service.knowledge.bo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * AI 知识库段落搜索 Request BO
 *
 * @author 乾乾
 */
@Data
@Accessors(chain = true)
public class AiKnowledgeSegmentSearchReqBO {

    /**
     * 知识库编号
     */
    @NotNull(message = "知识库编号不能为空")
    private Long knowledgeId;

    /**
     * 内容
     */
    @NotEmpty(message = "内容不能为空")
    private String content;

    /**
     * 最大返回数量
     */
    private Integer topK;

    /**
     * 相似度阈值
     */
    private Double similarityThreshold;

}