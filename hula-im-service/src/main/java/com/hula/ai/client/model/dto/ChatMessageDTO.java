package com.hula.ai.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  对话消息对象 DTO
 *
 * @author: 云裂痕
 * @date: 2025-03-08
 * 得其道 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 聊天记录id
     */
    private Long chatId;

    /**
     * 关联消息id
     */
    private String parentMessageId;

    /**
     * 消息id
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
     * 聊天摘要
     */
    private String role;

    /**
     * 聊天摘要
     */
    private String content;

    /**
     * 消息类型 text、image
     */
    private String contentType;

    /**
     * 回复状态
     */
    private Integer status;

}
