package com.hula.ai.controller.music.vo;

import com.hula.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hula.utils.DateUtil.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - AI 音乐分页 Request VO")
@Data
public class AiMusicPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "12212")
    private Long userId;

    @Schema(description = "音乐名称", example = "夜空中最亮的星")
    private String title;

    @Schema(description = "音乐状态", example = "20")
    private Integer status;

    @Schema(description = "生成模式", example = "1")
    private Integer generateMode;

    @Schema(description = "是否发布", example = "true")
    private Boolean publicStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}