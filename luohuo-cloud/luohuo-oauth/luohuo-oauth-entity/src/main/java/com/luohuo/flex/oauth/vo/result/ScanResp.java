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

    public ScanResp(String ip, Long expireTime) {
		this.ip = ip;
        this.expireTime = expireTime;
    }
}