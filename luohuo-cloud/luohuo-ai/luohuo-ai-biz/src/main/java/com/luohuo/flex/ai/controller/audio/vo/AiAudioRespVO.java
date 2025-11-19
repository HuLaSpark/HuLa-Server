package com.luohuo.flex.ai.controller.audio.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "AI 音频 Response VO")
@Data
public class AiAudioRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "用户编号", example = "28987")
    private Long userId;

    @Schema(description = "提示词", example = "你好，欢迎使用AI语音合成")
    private String prompt;

    @Schema(description = "平台", example = "SiliconFlow")
    private String platform;

    @Schema(description = "模型编号", example = "1024")
    private Long modelId;

    @Schema(description = "模型标识", example = "fishaudio/fish-speech-1.5")
    private String model;

    @Schema(description = "生成状态", example = "20")
    private Integer status;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "音频地址")
    private String audioUrl;

    @Schema(description = "是否公开", example = "false")
    private Boolean publicStatus;

    @Schema(description = "生成参数")
    private Map<String, Object> options;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
