package com.luohuo.flex.ws.vo;

import lombok.Data;

@Data
public class UserJoinRoomVO {
	private String uid;
	private String roomId;
	private String name;
	private String avatar;
	private long timestamp;     // 加入时间戳

	public UserJoinRoomVO(Long uid, Long roomId) {
		this.uid = uid + "";
		this.roomId = roomId + "";
		this.timestamp = System.currentTimeMillis();
	}
}