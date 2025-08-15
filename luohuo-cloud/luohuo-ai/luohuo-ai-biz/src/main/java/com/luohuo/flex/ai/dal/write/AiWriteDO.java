package com.luohuo.flex.ai.dal.write;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.flex.ai.dal.BaseDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.AiWriteTypeEnum;
import com.luohuo.flex.ai.enums.DictTypeConstants;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AI 写作 DO
 *
 */
@TableName("ai_write")
@KeySequence("ai_write_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Accessors(chain = true)
public class AiWriteDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     *
     * 关联 AdminUserDO 的 userId 字段
     */
    private Long userId;

    /**
     * 写作类型
     * <p>
     * 枚举 {@link AiWriteTypeEnum}
     */
    private Integer type;

    /**
     * 平台
     *
     * 枚举 {@link AiPlatformEnum}
     */
    private String platform;
    /**
     * 模型编号
     *
     * 关联 {@link AiModelDO#getId()}
     */
    private Long modelId;
    /**
     * 模型
     */
    private String model;

    /**
     * 生成内容提示
     */
    private String prompt;

    /**
     * 生成的内容
     */
    private String generatedContent;
    /**
     * 原文
     */
    private String originalContent;

    /**
     * 长度提示词
     *
     * 字典：{@link DictTypeConstants#AI_WRITE_LENGTH}
     */
    private Integer length;
    /**
     * 格式提示词
     *
     * 字典：{@link DictTypeConstants#AI_WRITE_FORMAT}
     */
    private Integer format;
    /**
     * 语气提示词
     *
     * 字典：{@link DictTypeConstants#AI_WRITE_TONE}
     */
    private Integer tone;
    /**
     * 语言提示词
     *
     * 字典：{@link DictTypeConstants#AI_WRITE_LANGUAGE}
     */
    private Integer language;

    /**
     * 错误信息
     */
    private String errorMessage;

}