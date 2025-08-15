package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 朋友圈发的图片或者视频表
 */
@TableName("im_feed_media")
@Data
public class FeedMedia extends SuperEntity<Long> {

    @Schema(description = "朋友圈ID")
    private Long feedId;

	@Schema(description = "图片或视频的路径")
	private String url;

    @Schema(description = "排序")
    private Integer sort;
}