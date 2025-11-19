package com.luohuo.flex.ai.dal.music;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.luohuo.flex.ai.dal.BaseDO;
import com.luohuo.flex.ai.enums.AiMusicGenerateModeEnum;
import com.luohuo.flex.ai.enums.AiMusicStatusEnum;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * AI 音乐 DO
 *
 * @author xiaoxin
 */
@TableName(value = "ai_music", autoResultMap = true)
@KeySequence("ai_music_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Accessors(chain = true)
public class AiMusicDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     * <p>
     * 关联 AdminUserDO 的 userId 字段
     */
    private Long userId;

    /**
     * 音乐名称
     */
    private String title;

    /**
     * 歌词
     */
    private String lyric;

    /**
     * 图片地址
     */
    private String imageUrl;
    /**
     * 音频地址
     */
    private String audioUrl;
    /**
     * 视频地址
     */
    private String videoUrl;

    /**
     * 音乐状态
     * <p>
     * 枚举 {@link AiMusicStatusEnum}
     */
    private Integer status;

    /**
     * 生成模式
     * <p>
     * 枚举 {@link AiMusicGenerateModeEnum}
     */
    private Integer generateMode;

    /**
     * 描述词
     */
    private String description;

    /**
     * 平台
     * <p>
     * 枚举 {@link AiPlatformEnum}
     */
    private String platform;
    // TODO @芋艿：modelId？
    /**
     * 模型
     */
    private String model;

    /**
     * 音乐风格标签
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 音乐时长
     */
    private Double duration;

    /**
     * 是否公开
     */
    private Boolean publicStatus;

    /**
     * 任务编号
     */
    private String taskId;

    /**
     * 错误信息
     */
    private String errorMessage;

}
