package com.luohuo.flex.oauth.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "二维码状态")
public class QrCodeStatus implements Serializable {
    @Schema(description = "二维码唯一标识(用于轮询状态)")
    private String status;

	@Schema(description = "设备指纹")
	private String deviceHash;

	@Schema(description = "系统id")
	private Long userId;

	@Schema(description = "用户id")
	private Long uid;

    public QrCodeStatus(String status, String deviceHash) {
        this.status = status;
		this.deviceHash = deviceHash;
    }

	public QrCodeStatus(String status, String deviceHash, Long userId, Long uid) {
		this.status = status;
		this.userId = userId;
		this.uid = uid;
		this.deviceHash = deviceHash;
	}

	public QrCodeStatus() {
	}
}