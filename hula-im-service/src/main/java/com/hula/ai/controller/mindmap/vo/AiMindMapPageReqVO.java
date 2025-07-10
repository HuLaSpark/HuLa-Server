package com.hula.ai.controller.mindmap.vo;

import com.hula.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hula.utils.DateUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;


@Schema(description = "管理后台 - AI 思维导图分页 Request VO")
@Data
public class AiMindMapPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "4325")
    private Long userId;

    @Schema(description = "生成内容提示", example = "Java 学习路线")
    private String prompt;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}