package com.luohuo.flex.oauth.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "二维码状态")
public class QrCodeResult implements Serializable {
    @Schema(description = "二维码状态")
    private String status;

	@Schema(description = "数据")
	private Object data;

	public QrCodeResult(String status) {
		this.status = status;
	}

    public QrCodeResult(String status, Object data) {
        this.status = status;
		this.data = data;
    }

	public QrCodeResult() {
	}
}