package com.luohuo.flex.ai.controller.platform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 平台添加模型 Request VO
 *
 * @author 乾乾
 */
@Schema(description = "管理后台 - AI 平台添加模型 Request VO")
@Data
public class AiPlatformAddModelReqVO {

    @Schema(description = "平台代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "OpenAI")
    @NotBlank(message = "平台代码不能为空")
    private String platform;

    @Schema(description = "模型标志", requiredMode = Schema.RequiredMode.REQUIRED, example = "gpt-4")
    @NotBlank(message = "模型标志不能为空")
    private String model;

}
