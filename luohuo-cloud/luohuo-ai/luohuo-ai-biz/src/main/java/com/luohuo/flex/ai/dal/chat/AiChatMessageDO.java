package com.luohuo.flex.ai.dal.chat;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.flex.ai.dal.BaseDO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeSegmentDO;
import com.luohuo.flex.ai.dal.model.AiChatRoleDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiChatMessageContentTypeEnum;
import com.luohuo.flex.ai.handler.LongListTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.ai.chat.messages.MessageType;

import java.util.List;

/**
 * AI Chat 消息 DO
 */
@TableName(value = "ai_chat_message", autoResultMap = true)
@KeySequence("ai_chat_conversation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AiChatMessageDO extends BaseDO {

    /**
     * 编号，作为每条聊天记录的唯一标识符
     */
    @TableId
    private Long id;

    /**
     * 对话编号
     *
     * 关联 {@link AiChatConversationDO#getId()} 字段
     */
    private Long conversationId;
    /**
     * 回复消息编号
     *
     * 关联 {@link #id} 字段
     *
     * 大模型回复的消息编号，用于“问答”的关联
     */
    private Long replyId;

    /**
     * 消息类型
     *
     * 也等价于 OpenAPI 的 role 字段
     *
     * 枚举 {@link MessageType}
     */
    private String type;
    /**
     * 用户编号
     *
     * 关联 AdminUserDO 的 userId 字段
     */
    private Long userId;
    /**
     * 角色编号
     *
     * 关联 {@link AiChatRoleDO#getId()} 字段
     */
    private Long roleId;

    /**
     * 模型标志
     *
     * 冗余 {@link AiModelDO#getModel()}
     */
    private String model;
    /**
     * 模型编号
     *
     * 关联 {@link AiModelDO#getId()} 字段
     */
    private Long modelId;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 推理思考内容（用于支持思考模型，如 DeepSeek R1）
     */
    private String reasoningContent;

    /**
     * 是否携带上下文
     */
    private Boolean useContext;

    /**
     * 知识库段落编号数组
     *
     * 关联 {@link AiKnowledgeSegmentDO#getId()} 字段
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> segmentIds;

    /**
     * 消息内容类型
     *
     * 枚举 {@link AiChatMessageContentTypeEnum}
     *
     * 用于标记消息内容的类型（文本、图片、视频、音频等），方便前端渲染
     */
    private Integer msgType;

	/**
	 * 租户id
	 */
	private Long tenantId;
}
