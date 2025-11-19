package com.luohuo.flex.im.domain.vo.request.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 聊天公告
 */
@Data
public class AnnouncementsParam implements Serializable {

	@Schema(description = "公告id")
	private Long id;

	@NotNull(message = "请选择群聊")
	private Long roomId;

	@NotEmpty(message = "请输入公告内容")
	private String content;

	@Schema(description = "置顶")
	private Boolean top;
}
