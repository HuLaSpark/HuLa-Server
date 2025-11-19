package com.luohuo.flex.oauth.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "轮询二维码状态")
public class QueryStatusReq implements Serializable {
	@NotEmpty(message = "二维码参数不能为空")
    private String qrId;

	@NotEmpty(message = "设备信息异常")
	@Schema(description = "前端指纹")
	private String clientId;

	@NotEmpty(message = "设备异常")
	private String deviceHash;

	@Pattern(regexp = "^(PC)$", message = "登录方式只能是 PC")
	@NotEmpty(message = "登录方式只能是 PC")
	@Schema(description = "请选择登录方式 PC")
	private String deviceType;
}