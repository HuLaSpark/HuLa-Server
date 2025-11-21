package com.luohuo.flex.ai.controller.model.vo.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI API 密钥简单信息 Response VO")
@Data
public class AiApiKeySimpleRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23538")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "文心一言")
    private String name;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "OpenAI")
    private String platform;
}
