package com.hula.ai.gpt.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  对话消息对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 聊天记录id
     */
    private Long chatId;

    /**
     * 聊天摘要
     */
    private String chatTitle;

    /**
     * 关联消息id
     */
    private String parentMessageId;

    /**
     * 第三方消息id
     */
    private String messageId;

    /**
     * 使用模型
     */
    private String model;

    /**
     * 使用模型版本
     */
    private String modelVersion;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型 text、image
     */
    private String contentType;

    /**
     * 角色模型
     */
    private String role;

    /**
     * finish_reason
     */
    private String finishReason;

    /**
     * 状态 1 回复中 2正常 3 失败
     */
    private Integer status;

    /**
     * 调用token
     */
    private String appKey;

    /**
     * 使用额度
     */
    private Long usedTokens;

    /**
     * 响应全文
     */
    private String response;

}
