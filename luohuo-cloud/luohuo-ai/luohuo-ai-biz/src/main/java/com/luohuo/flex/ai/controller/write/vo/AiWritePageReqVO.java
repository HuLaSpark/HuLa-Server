package com.luohuo.flex.ai.controller.write.vo;


import com.luohuo.flex.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.luohuo.basic.utils.TimeUtils.DEFAULT_YEAR_FORMAT;


@Schema(description = "管理后台 - AI 写作分页 Request VO")
@Data
public class AiWritePageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "28404")
    private Long userId;

    @Schema(description = "写作类型", example = "1")
    private Integer type;

    @Schema(description = "平台", example = "TongYi")
    private String platform;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DEFAULT_YEAR_FORMAT)
    private LocalDateTime[] createTime;

}