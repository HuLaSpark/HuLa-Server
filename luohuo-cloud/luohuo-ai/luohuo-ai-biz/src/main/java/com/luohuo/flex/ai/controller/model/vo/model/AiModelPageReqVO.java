package com.luohuo.flex.ai.controller.model.vo.model;

import com.luohuo.flex.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - API 模型分页 Request VO")
@Data
public class AiModelPageReqVO extends PageParam {

    @Schema(description = "模型名字", example = "张三")
    private String name;

    @Schema(description = "模型标识", example = "gpt-3.5-turbo-0125")
    private String model;

    @Schema(description = "模型平台", example = "OpenAI")
    private String platform;

    @Schema(description = "是否公开：0-公开，1-私有", example = "0")
    private Integer publicStatus;

}