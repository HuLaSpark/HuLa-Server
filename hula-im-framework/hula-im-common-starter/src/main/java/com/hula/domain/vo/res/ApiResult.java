package com.hula.domain.vo.res;

import com.hula.common.MDCKey;
import com.hula.enums.HttpErrorEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.slf4j.MDC;

/**
 * 通用返回体
 * @author nyh
 */
@Data
@Schema(description ="基础返回体")
public class ApiResult<T> {
    @Schema(description ="成功标识。true/false")
    private Boolean success;
    @Schema(description ="状态码")
    private Integer code;
    @Schema(description ="错误消息")
    private String msg;
    @Schema(description ="追踪id")
    private String tid;
    @Schema(description ="返回对象")
    private T data;

    private ApiResult() {
        this.setTid(MDC.get(MDCKey.TID));
    }

    public static <T> ApiResult<T> success() {
        ApiResult<T> result = new ApiResult<>();
        result.setData(null);

        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setData(data);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

	public static <T> ApiResult<T> tokenExceed(HttpErrorEnum httpError) {
		ApiResult<T> result = new ApiResult<>();
		result.setSuccess(Boolean.TRUE);
		result.setCode(httpError.getCode());
		result.setMsg(httpError.getMsg());
		return result;
	}

    public static <T> ApiResult<T> fail(Integer code, String msg) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(Boolean.FALSE);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
