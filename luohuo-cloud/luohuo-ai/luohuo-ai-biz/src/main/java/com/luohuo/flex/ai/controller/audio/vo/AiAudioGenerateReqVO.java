package com.luohuo.flex.ai.controller.audio.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Schema(description = "AI 音频生成 Request VO")
@Data
public class AiAudioGenerateReqVO {

    @Schema(description = "模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "模型编号不能为空")
    private Long modelId;

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好，欢迎使用AI语音合成")
    @NotEmpty(message = "提示词不能为空")
    @Size(max = 2000, message = "提示词最大 2000")
    private String prompt;

    @Schema(description = "对话编号（可选，用于关联到聊天对话）", example = "1024")
    private Long conversationId;

    @Schema(description = "生成参数")
    private Map<String, String> options;
}
