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

    public QrCodeStatus(String status, String deviceHash) {
        this.status = status;
		this.deviceHash = deviceHash;
    }

	public QrCodeStatus() {
	}
}