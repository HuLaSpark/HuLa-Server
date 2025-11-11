package com.luohuo.flex.ai.dal.audio;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.luohuo.flex.ai.dal.BaseDO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI 音频生成 DO
 *
 * @author 乾乾
 */
@TableName(value = "ai_audio", autoResultMap = true)
@KeySequence("ai_audio_seq")
@Data
@Accessors(chain = true)
public class AiAudioDO extends BaseDO {

    /**
     * 音频编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 平台
     */
    private String platform;

    /**
     * 模型编号
     */
    private Long modelId;

    /**
     * 模型标识
     */
    private String model;

    /**
     * 生成状态
     */
    private Integer status;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 音频 URL
     */
    private String audioUrl;

    /**
     * 是否发布
     */
    private Boolean publicStatus;

    /**
     * 生成参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> options;

    /**
     * 任务编号（平台返回）
     */
    private String taskId;

    /**
     * 对话编号
     *
     * {@link com.luohuo.flex.ai.dal.chat.AiChatConversationDO#getId()}
     */
    private Long conversationId;

    /**
     * 对话消息编号
     *
     * {@link com.luohuo.flex.ai.dal.chat.AiChatMessageDO#getId()}
     */
    private Long chatMessageId;
}
