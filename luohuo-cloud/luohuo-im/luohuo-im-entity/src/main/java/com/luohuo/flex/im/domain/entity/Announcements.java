package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 聊天公告
 */
@Data
@TableName("im_announcements")
public class Announcements extends Entity<Long> {

	@Schema(description = "群id")
	private Long roomId;

	@Schema(description = "公告发布人")
	private Long uid;

	@Schema(description = "发布内容")
	private String content;

	@Schema(description = "置顶")
	private Boolean top;
}
