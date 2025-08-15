package com.luohuo.flex.im.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author nyh
 */
@Data
public class IpResult<T> implements Serializable {
    @Schema(description = "错误码")
    private Integer code;
    @Schema(description = "错误消息")
    private String msg;
	@Schema(description = "返回对象")
    private T data;

    public boolean isSuccess() {
        return Objects.nonNull(this.code) && this.code == 0;
    }
}
