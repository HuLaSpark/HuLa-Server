package com.luohuo.flex.ai.dal.model;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.flex.ai.dal.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AI API 秘钥 DO

 */
@TableName("ai_api_key")
@KeySequence("ai_chat_conversation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AiApiKeyDO extends BaseDO {

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
     * 名称
     */
    private String name;
    /**
     * 密钥
     */
    private String apiKey;
    /**
     * 平台
     *
     * 枚举 {@link AiPlatformEnum}
     */
    private String platform;
    /**
     * API 地址
     */
    private String url;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 是否公开
     *
     * 1. true - 公开；由管理员在【API 秘钥管理】所创建
     * 2. false - 私有；由个人在【我的 API 秘钥】所创建
     */
    private Boolean publicStatus;

}
