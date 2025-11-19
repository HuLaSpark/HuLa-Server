package com.luohuo.flex.ws.vo;

import lombok.Data;

/**
 * 屏幕共享状态VO
 */
@Data
public class ScreenSharingVO {
    private Long roomId;       // 房间ID
    private Long userId;       // 共享者用户ID
    private boolean sharing;   // 是否正在共享

	public ScreenSharingVO() {
	}

	public ScreenSharingVO(Long roomId, Long userId, boolean sharing) {
		this.roomId = roomId;
		this.userId = userId;
		this.sharing = sharing;
	}
}
