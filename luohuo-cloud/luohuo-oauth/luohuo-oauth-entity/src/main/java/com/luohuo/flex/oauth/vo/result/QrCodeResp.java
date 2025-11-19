package com.luohuo.flex.oauth.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "二维码生成响应体")
public class QrCodeResp implements Serializable {
    @Schema(description = "二维码唯一标识(用于轮询状态)")
    private String qrId;

	@Schema(description = "设备指纹")
	private String deviceHash;

    @Schema(description = "二维码过期时间戳(毫秒)")
    private Long expireTime;

	@Schema(description = "登录端ip")
	private String ip;

    public QrCodeResp(String qrId, String deviceHash, String ip, Long expireTime) {
        this.qrId = qrId;
		this.deviceHash = deviceHash;
        this.expireTime = expireTime;
		this.ip = ip;
    }
}