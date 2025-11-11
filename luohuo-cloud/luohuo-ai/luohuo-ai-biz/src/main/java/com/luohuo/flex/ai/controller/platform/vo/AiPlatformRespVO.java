package com.luohuo.flex.ai.controller.platform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI 平台配置 Response VO
 *
 * @author 乾乾
 */
@Schema(description = "管理后台 - AI 平台配置 Response VO")
@Data
public class AiPlatformRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "平台代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "OpenAI")
    private String platform;

    @Schema(description = "平台名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "OpenAI")
    private String name;

    @Schema(description = "平台显示标签", requiredMode = Schema.RequiredMode.REQUIRED, example = "OpenAI")
    private String label;

    @Schema(description = "模型示例", example = "gpt-4, gpt-4-turbo, gpt-3.5-turbo")
    private String examples;

    @Schema(description = "文档链接", example = "https://platform.openai.com/docs/models")
    private String docs;

    @Schema(description = "提示信息", example = "请前往 OpenAI 官网查看可用模型列表")
    private String hint;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

}
