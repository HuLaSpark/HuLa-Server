package com.hula.core.chat.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 房间信息修改广播
 * Description: 消息发送请求体
 *
 * @author 乾乾
 * @date 2025-02-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomGroupChange {
	/**
	 * 房间id
	 */
	private Long roomId;

	/**
	 * 群名称
	 */
	private String name;

	/**
	 * 群头像
	 */
	private String avatar;
}
