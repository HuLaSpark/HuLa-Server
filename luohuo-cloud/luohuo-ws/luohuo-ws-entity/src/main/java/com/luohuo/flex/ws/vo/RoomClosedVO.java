package com.luohuo.flex.ws.vo;

import lombok.Data;

@Data
public class RoomClosedVO {
    private String roomId;
	// 行为操作: 超时关闭，发起通话的人关闭
    private String reason;

	public RoomClosedVO() {
	}

	public RoomClosedVO(Long roomId, String reason) {
		this.reason = reason;
		this.roomId = roomId+"";
	}
}