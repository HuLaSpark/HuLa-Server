package com.luohuo.flex.im.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告内容
 */
@Data
public class AnnouncementsResp implements Serializable {

	@Schema(description = "公告id")
	private Long id;

	@Schema(description = "群id")
	private Long roomId;

	@Schema(description = "公告发布人id")
	private Long uid;

	@Schema(description = "发布内容")
	private String content;

	@Schema(description = "发布时间")
	private LocalDateTime createTime;

	@Schema(description = "已读数量")
	private Long count;

	@Schema(description = "置顶")
	private Boolean top;
}
