package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 朋友圈表
 */
@TableName("im_feed")
@Data
public class Feed extends SuperEntity<Long> {

    @Schema(description = "发布人id")
	private Long uid;

    @Schema(description = "朋友圈文案")
	private String content;

	@Schema(description = "privacy -> 私密 open -> 公开 partVisible -> 部分可见 notAnyone -> 不给谁看")
	private String permission;

	@Schema(description = "朋友圈内容类型（0: 纯文字 1: 图片, 2: 视频）")
	private Integer mediaType;
}