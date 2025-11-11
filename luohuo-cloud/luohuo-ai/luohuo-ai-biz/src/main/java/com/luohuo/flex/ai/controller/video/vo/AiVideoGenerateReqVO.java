package com.luohuo.flex.ai.controller.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - AI 视频生成 Request VO")
@Data
public class AiVideoGenerateReqVO {

    @Schema(description = "模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "模型编号不能为空")
    private Long modelId;

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "生成一段海边日落的视频")
    @NotEmpty(message = "提示词不能为空")
    @Size(max = 2000, message = "提示词长度不能超过 2000")
    private String prompt;

    @Schema(description = "视频高度", example = "720")
    private Integer height;

    @Schema(description = "视频宽度", example = "1280")
    private Integer width;

    @Schema(description = "视频时长（秒）", example = "5")
    private Integer duration;

    @Schema(description = "会话编号，如果在对话中生成视频，需要传入会话编号", example = "1024")
    private Long conversationId;

    /**
     * 生成参数，根据不同 platform 传入不同的参数
     */
    @Schema(description = "生成参数")
    private Map<String, String> options;

}