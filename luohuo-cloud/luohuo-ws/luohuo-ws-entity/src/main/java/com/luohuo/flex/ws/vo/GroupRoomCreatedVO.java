package com.luohuo.flex.ws.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 群组视频房间创建通知VO
 */
@Data
@AllArgsConstructor
public class GroupRoomCreatedVO {
    private Long roomId;       // 房间ID
    private Long creatorUid;   // 创建者用户ID
    private boolean isVideo;   // 是否为视频房间

	public GroupRoomCreatedVO() {
	}

	public GroupRoomCreatedVO(Long roomId) {
		this.roomId = roomId;
	}
}