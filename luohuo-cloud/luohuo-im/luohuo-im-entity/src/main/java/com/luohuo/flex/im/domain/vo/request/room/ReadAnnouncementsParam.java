package com.luohuo.flex.im.domain.vo.request.room;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 已读公告
 */
@Data
public class ReadAnnouncementsParam implements Serializable {

	@NotNull(message = "请选择群聊")
	private Long roomId;

	@NotNull(message = "请选择公告")
	private Long announcementId;
}
