package com.luohuo.flex.ai.service.knowledge.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AI 知识库段落搜索 Response BO
 *
 * @author 乾乾
 */
@Data
@Accessors(chain = true)
public class AiKnowledgeSegmentSearchRespBO {

    /**
     * 段落编号
     */
    private Long id;
    /**
     * 文档编号
     */
    private Long documentId;
    /**
     * 知识库编号
     */
    private Long knowledgeId;

    /**
     * 内容
     */
    private String content;
    /**
     * 内容长度
     */
    private Integer contentLength;

    /**
     * Token 数量
     */
    private Integer tokens;

    /**
     * 相似度分数
     */
    private Double score;

}