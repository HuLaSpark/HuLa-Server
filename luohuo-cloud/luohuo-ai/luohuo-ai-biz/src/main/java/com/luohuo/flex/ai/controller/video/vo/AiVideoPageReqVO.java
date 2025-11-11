package com.luohuo.flex.ai.controller.video.vo;

import com.luohuo.flex.ai.common.pojo.PageParam;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.luohuo.basic.utils.TimeUtils.DEFAULT_YEAR_FORMAT;

/**
 * 管理后台 - AI 视频分页 Request VO
 *
 * @author 乾乾
 */
@Data
public class AiVideoPageReqVO extends PageParam {

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 平台
     */
    private String platform;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 生成状态
     */
    private Integer status;

    /**
     * 是否发布
     */
    private Boolean publicStatus;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = DEFAULT_YEAR_FORMAT)
    private LocalDateTime[] createTime;

}
