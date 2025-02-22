package com.hula.core.chat.domain.vo.request.room;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天公告
 */
@Data
public class AnnouncementsParam implements Serializable {

	@NotNull(message = "请选择群聊")
	private Long roomId;

	private Long uid;

	@NotEmpty(message = "请输入公告内容")
	private String content;

	private LocalDateTime publishTime;
}
