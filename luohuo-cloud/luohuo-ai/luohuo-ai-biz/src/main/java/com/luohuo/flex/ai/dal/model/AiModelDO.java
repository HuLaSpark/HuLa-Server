package com.luohuo.flex.ai.dal.model;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.flex.ai.dal.BaseDO;
import com.luohuo.flex.ai.enums.AiModelTypeEnum;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.CommonStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AI 模型 DO
 *
 * 默认模型：{@link #status} 为开启，并且 {@link #sort} 排序第一
 */
@TableName("ai_model")
@KeySequence("ai_model_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AiModelDO extends BaseDO {

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
     * API 秘钥编号
     *
     * 关联 {@link AiApiKeyDO#getId()}
     */
    private Long keyId;
    /**
     * 模型名称
     */
    private String name;
    /**
     * 模型头像
     */
    private String avatar;
    /**
     * 模型标志
     */
    private String model;
    /**
     * 平台
     *
     * 枚举 {@link AiPlatformEnum}
     */
    private String platform;
    /**
     * 类型
     *
     * 枚举 {@link AiModelTypeEnum}
     */
    private Integer type;

    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 是否公开
     *
     * 0 - 公开；系统级别模型，所有用户可见
     * 1 - 私有；用户专属模型，仅创建者可见
     */
    private Integer publicStatus;

    // ========== 对话配置 ==========

    /**
     * 温度参数
     *
     * 用于调整生成回复的随机性和多样性程度：较低的温度值会使输出更收敛于高频词汇，较高的则增加多样性
     */
    private Double temperature;
    /**
     * 单条回复的最大 Token 数量
     */
    private Integer maxTokens;
    /**
     * 上下文的最大 Message 数量
     */
    private Integer maxContexts;

    /**
     * 是否支持深度思考模式
     */
    private Boolean supportsReasoning;

}
