package com.hula.core.chat.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 房间里自己信息修改广播
 * Description: 消息发送请求体
 *
 * @author 乾乾
 * @date 2025-02-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMyRoomGroupChange {
	/**
	 * 房间id
	 */
	private Long roomId;

	/**
	 * 我在群里的名称
	 */
	private String myName;
}
