package com.luohuo.flex.ai.controller.workflow.vo;

import com.luohuo.flex.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.luohuo.basic.utils.TimeUtils.DEFAULT_YEAR_FORMAT;


@Schema(description = "管理后台 - AI 工作流分页 Request VO")
@Data
public class AiWorkflowPageReqVO extends PageParam {

    @Schema(description = "名称", example = "工作流")
    private String name;

    @Schema(description = "标识", example = "FLOW")
    private String code;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DEFAULT_YEAR_FORMAT)
    private LocalDateTime[] createTime;

}
