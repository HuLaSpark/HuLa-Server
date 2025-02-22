package com.hula.core.chat.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告内容
 */
@Data
public class AnnouncementsResp implements Serializable {

	@Schema(description = "群id")
	private Long roomId;

	@Schema(description = "公告发布人")
	private Long uid;

	@Schema(description = "发布内容")
	private String content;

	@Schema(description = "发布时间")
	private LocalDateTime publishTime;

	@Schema(description = "已读数量")
	private Long count;
}
