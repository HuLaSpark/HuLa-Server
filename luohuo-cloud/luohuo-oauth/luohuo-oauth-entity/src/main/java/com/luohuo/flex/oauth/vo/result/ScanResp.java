package com.luohuo.flex.oauth.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "扫码后展示在手机端的信息")
public class ScanResp implements Serializable {
    @Schema(description = "二维码过期时间戳(毫秒)")
    private Long expireTime;

	@Schema(description = "登录端ip")
	private String ip;

	@Schema(description = "登录设备")
	private String deviceType;

	@Schema(description = "登录地址")
	private String locPlace;

	public ScanResp(String ip, Long expireTime, String deviceType) {
		this.ip = ip;
		this.expireTime = expireTime;
		this.deviceType = deviceType;
	}
}