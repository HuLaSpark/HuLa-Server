package com.luohuo.flex.model.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReadMessageDTO {
	/**
	 * 消息id
	 */
	private List<Long> msgId;

	/**
	 * 回执的uid
	 */
	private Long uid;
}