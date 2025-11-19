package com.luohuo.flex.ai.controller.mindmap.vo;

import com.luohuo.flex.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.luohuo.basic.utils.TimeUtils.DEFAULT_YEAR_FORMAT;


@Schema(description = "管理后台 - AI 思维导图分页 Request VO")
@Data
public class AiMindMapPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "4325")
    private Long userId;

    @Schema(description = "生成内容提示", example = "Java 学习路线")
    private String prompt;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DEFAULT_YEAR_FORMAT)
    private LocalDateTime[] createTime;

}