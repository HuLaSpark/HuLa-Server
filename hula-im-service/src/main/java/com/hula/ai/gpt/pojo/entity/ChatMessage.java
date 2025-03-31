package com.hula.ai.gpt.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话消息对象 ai_gpt_chat_message
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
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("ai_gpt_chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	protected Long id;

	@Schema(description = "创建时间")
	protected LocalDateTime createdTime;

	@Schema(description = "创建人ID")
	protected Long createdBy;

	@Schema(description = "最后修改时间")
	protected LocalDateTime updatedTime;

	@Schema(description = "最后修改人ID")
	protected Long updatedBy;

	@JsonIgnore
	@TableLogic(value = "false", delval = "true")
	protected Boolean isDel;

    /**
     * 聊天记录id
     */
    private Long chatId;

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
     * 角色模型
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型 text、image
     */
    @Builder.Default
    private String contentType = "text";

    /**
     * 结束原因
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
