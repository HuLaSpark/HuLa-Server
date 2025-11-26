package com.luohuo.flex.ai.controller.model.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 模型 Response VO")
@Data
@Accessors(chain = true)
public class AiModelRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2630")
    private Long id;

    @Schema(description = "API 秘钥编号", example = "22042")
    private Long keyId;

	@Schema(description = "创建人", example = "22042")
	private Long userId;

    @Schema(description = "模型名字", example = "张三")
    private String name;

    @Schema(description = "模型头像", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "模型标识", example = "gpt-3.5-turbo-0125")
    private String model;

    @Schema(description = "模型平台", example = "OpenAI")
    private String platform;

    @Schema(description = "模型类型", example = "1")
    private Integer type;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "温度参数", example = "1")
    private Double temperature;

    @Schema(description = "单条回复的最大 Token 数量", example = "4096")
    private Integer maxTokens;

    @Schema(description = "上下文的最大 Message 数量", example = "8192")
    private Integer maxContexts;

    @Schema(description = "是否支持深度思考模式", example = "true")
    private Boolean supportsReasoning;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

	@Schema(description = "是否公开：0-公开，1-私有", example = "0")
	private Integer publicStatus;

}