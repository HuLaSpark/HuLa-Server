package com.luohuo.flex.oauth.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户扫码成功后的信息")
public class LoginUserInfo implements Serializable {
    @Schema(description = "二维码唯一标识")
    private String status;

	@Schema(description = "系统id")
	private Long userId;

	@Schema(description = "用户id")
	private Long uid;

	@Schema(description = "用户指纹")
	private String deviceHash;

    public LoginUserInfo(String status, String deviceHash, Long userId, Long uid) {
        this.status = status;
		this.userId = userId;
		this.uid = uid;
		this.deviceHash = deviceHash;
    }

	public LoginUserInfo() {
	}
}