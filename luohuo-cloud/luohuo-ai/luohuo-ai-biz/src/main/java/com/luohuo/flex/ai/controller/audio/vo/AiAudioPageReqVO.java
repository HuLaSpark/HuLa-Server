package com.luohuo.flex.ai.controller.audio.vo;

import com.luohuo.flex.ai.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.luohuo.basic.utils.DateUtils.DEFAULT_YEAR_FORMAT;


@Schema(description = "AI 音频分页 Request VO")
@Data
public class AiAudioPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "28987")
    private Long userId;

    @Schema(description = "平台", example = "SiliconFlow")
    private String platform;

    @Schema(description = "提示词", example = "生成一段舒缓的音乐")
    private String prompt;

    @Schema(description = "生成状态", example = "20")
    private Integer status;

    @Schema(description = "是否发布", example = "true")
    private Boolean publicStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DEFAULT_YEAR_FORMAT)
    private LocalDateTime[] createTime;
}