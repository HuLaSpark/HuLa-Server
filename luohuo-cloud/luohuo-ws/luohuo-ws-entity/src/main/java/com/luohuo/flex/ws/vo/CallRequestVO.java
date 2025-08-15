package com.luohuo.flex.ws.vo;

import lombok.Data;

@Data
public class CallRequestVO {
    private Long targetUid;
	private Long roomId;
    private boolean isVideo; // true=视频, false=语音

	public CallRequestVO() {
	}

	public CallRequestVO(Long roomId, Long targetUid, boolean isVideo) {
		this.roomId = roomId;
		this.targetUid = targetUid;
		this.isVideo = isVideo;
	}
}