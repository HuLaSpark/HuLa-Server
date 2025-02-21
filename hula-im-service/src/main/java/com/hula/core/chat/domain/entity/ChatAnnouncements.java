package com.hula.core.chat.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群公告
 */
@Data
public class ChatAnnouncements implements Serializable {

	private Long id;

	private Long groupId;

	private Long employeeId;

	private String content;

	private LocalDateTime publishTime;
}
