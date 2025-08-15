package com.luohuo.flex.ws.vo;

import lombok.Data;

@Data
public class VideoSignalVO {
    private Long senderId;		// 信令发送者ID
	private Long roomId;		// 房间id
    private String signal;		// WebRTC信令内容
	private String signalType;	// 会话类型
    private long timestamp;		// 发送时间戳

	public VideoSignalVO(Long senderId, Long roomId, String signalType, String signal) {
		this.senderId = senderId;
		this.roomId = roomId;
		this.signalType = signalType;
		this.signal = signal;
	}

	public VideoSignalVO() {
	}
}