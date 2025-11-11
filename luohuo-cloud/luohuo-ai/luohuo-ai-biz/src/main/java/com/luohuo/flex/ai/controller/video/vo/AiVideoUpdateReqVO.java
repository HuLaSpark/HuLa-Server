package com.luohuo.flex.ai.controller.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - AI 视频更新 Request VO
 *
 * @author 乾乾
 */
@Schema(description = "管理后台 - AI 视频更新 Request VO")
@Data
public class AiVideoUpdateReqVO {

    /**
     * 视频编号
     */
    @Schema(description = "视频编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    /**
     * 是否发布
     */
    @Schema(description = "是否发布", example = "true")
    private Boolean publicStatus;

}
