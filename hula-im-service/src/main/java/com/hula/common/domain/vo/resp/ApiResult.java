package com.hula.common.domain.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通用返回体
 * @author nyh
 */
@Data
@Schema(description = "基础返回体")
public class ApiResult<T> {
    @Schema(description = "成功标识true or false")
    private Boolean success;
    @Schema(description = "错误码")
    private Integer errCode;
    @Schema(description = "错误消息")
    private String errMsg;
    @Schema(description = "返回对象")
    private T data;

    public static <T> ApiResult<T> success() {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(null);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(data);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> fail(Integer code, String msg) {
        ApiResult<T> result = new ApiResult<T>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(code);
        result.setErrMsg(msg);
        return result;
    }

//    public static <T> ApiResult<T> fail(ErrorEnum errorEnum) {
//        ApiResult<T> result = new ApiResult<T>();
//        result.setSuccess(Boolean.FALSE);
//        result.setErrCode(errorEnum.getErrorCode());
//        result.setErrMsg(errorEnum.getErrorMsg());
//        return result;
//    }

    public boolean isSuccess() {
        return this.success;
    }
}
