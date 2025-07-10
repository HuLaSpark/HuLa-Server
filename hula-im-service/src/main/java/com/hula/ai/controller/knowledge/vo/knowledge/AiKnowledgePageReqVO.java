package com.hula.ai.controller.knowledge.vo.knowledge;

import com.hula.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hula.utils.DateUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - AI 知识库的分页 Request VO")
@Data
public class AiKnowledgePageReqVO extends PageParam {

    @Schema(description = "知识库名称", example = "芋艿")
    private String name;

    @Schema(description = "是否启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
