package com.luohuo.flex.ai.controller.model.vo.apikey;

import com.luohuo.flex.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI API 密钥分页 Request VO")
@Data
public class AiApiKeyPageReqVO extends PageParam {

    @Schema(description = "名称", example = "文心一言")
    private String name;

    @Schema(description = "平台", example = "OpenAI")
    private String platform;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "是否公开", example = "true")
    private Boolean publicStatus;

}