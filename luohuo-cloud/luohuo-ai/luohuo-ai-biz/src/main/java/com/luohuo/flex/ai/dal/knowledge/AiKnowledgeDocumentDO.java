package com.luohuo.flex.ai.dal.knowledge;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.flex.ai.dal.BaseDO;
import com.luohuo.flex.ai.enums.CommonStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AI 知识库-文档 DO
 *
 * @author xiaoxin
 */
@TableName(value = "ai_knowledge_document")
@KeySequence("ai_knowledge_document_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Accessors(chain = true)
public class AiKnowledgeDocumentDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 知识库编号
     * <p>
     * 关联 {@link AiKnowledgeDO#getId()}
     */
    private Long knowledgeId;
    /**
     * 文档名称
     */
    private String name;
    /**
     * 文件 URL
     */
    private String url;
    /**
     * 内容
     */
    private String content;
    /**
     * 文档长度
     */
    private Integer contentLength;

    /**
     * 文档 token 数量
     */
    private Integer tokens;
    /**
     * 分片最大 Token 数
     */
    private Integer segmentMaxTokens;

    /**
     * 召回次数
     */
    private Integer retrievalCount;

    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
