package com.luohuo.flex.ai.dal.video;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.luohuo.flex.ai.dal.BaseDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.AiVideoStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI 视频生成 DO
 *
 * @author 乾乾
 */
@Data
@TableName(value = "ai_video", autoResultMap = true)
@KeySequence("ai_video_seq")
@Accessors(chain = true)
public class AiVideoDO extends BaseDO {

    /**
     * 视频编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 对话编号
     */
    private Long conversationId;

    /**
     * 提示词
     */
    private String prompt;

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
     * 模型标识
     *
     * 关联 {@link AiModelDO#getModel()}
     */
    private String model;

    /**
     * 视频宽度
     */
    private Integer width;

    /**
     * 视频高度
     */
    private Integer height;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 生成状态
     *
     * 枚举 {@link AiVideoStatusEnum}
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
     * 视频 URL
     */
    private String videoUrl;

    /**
     * 封面 URL
     */
    private String coverUrl;

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
     * 对话消息编号
     */
    private Long chatMessageId;

}
