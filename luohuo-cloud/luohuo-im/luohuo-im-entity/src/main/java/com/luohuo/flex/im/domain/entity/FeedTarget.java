package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 朋友圈标签、用户可见表
 */
@TableName("im_feed_target")
@Data
public class FeedTarget extends SuperEntity<Long> {

    @Schema(description = "朋友圈ID")
    private Long feedId;

	/**
	 * @see Target
	 */
    @Schema(description = "标签ID")
    private Long targetId;

	@Schema(description = "1 -> 关联标签id 2 -> 关联用户id")
	private Integer type;
}