package com.luohuo.flex.model.entity.ws;

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
	private String roomId;

	/**
	 * 更改人id
	 */
	private String uid;

	/**
	 * 我在群里的名称
	 */
	private String myName;
}
