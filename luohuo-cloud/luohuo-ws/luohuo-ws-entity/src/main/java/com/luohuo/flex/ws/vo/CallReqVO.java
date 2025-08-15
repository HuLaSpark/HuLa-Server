package com.luohuo.flex.ws.vo;

import lombok.Data;

@Data
public class CallReqVO {
	private String callerUid;
    private String targetUid;
	private String roomId;
	private boolean isVideo; // true=视频, false=语音


	public CallReqVO() {
	}

	public CallReqVO(Long roomId, Long callerUid, Long targetUid, boolean isVideo) {
		this.callerUid = callerUid+"";
		this.roomId = roomId+"";
		this.targetUid = targetUid+"";
		this.isVideo = isVideo;
	}
}