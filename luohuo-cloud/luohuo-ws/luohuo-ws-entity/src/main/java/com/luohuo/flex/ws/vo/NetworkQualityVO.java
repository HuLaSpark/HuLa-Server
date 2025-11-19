package com.luohuo.flex.ws.vo;

import lombok.Data;

/**
 * 网络质量报告
 */
@Data
public class NetworkQualityVO {
    private Long roomId;       // 房间ID
    private Long userId;       // 用户ID
    private double quality;    // 网络质量评分 (0.0-1.0)
    private long timestamp;    // 报告时间戳

	public NetworkQualityVO() {
	}

	public NetworkQualityVO(Long roomId, Long userId, double quality) {
		this.roomId = roomId;
		this.userId = userId;
		this.quality = quality;
		timestamp = System.currentTimeMillis();
	}
}