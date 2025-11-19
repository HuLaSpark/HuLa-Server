package com.luohuo.flex.oauth.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "二维码请求")
public class ScanReq implements Serializable {
	@NotEmpty(message = "二维码参数不能为空")
    private String qrId;
}