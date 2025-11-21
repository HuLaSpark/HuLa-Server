package com.luohuo.basic.exception;

import com.luohuo.basic.exception.code.BaseExceptionCode;
import com.luohuo.basic.exception.code.ResponseEnum;

/**
 * 403  禁止访问
 *
 * @author 乾乾
 * @version 1.0
 */
public class ForbiddenException extends BaseUncheckedException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException(int code, String message) {
        super(code, message);
    }

    public ForbiddenException(int code, String message, Object... args) {
        super(code, message, args);
    }

    public static ForbiddenException wrap(BaseExceptionCode ex) {
        return new ForbiddenException(ex.getCode(), ex.getMsg());
    }

    public static ForbiddenException wrap(String msg) {
        return new ForbiddenException(ResponseEnum.FORBIDDEN.getCode(), msg);
    }

    public static ForbiddenException wrap(String msg, Object... args) {
        return new ForbiddenException(ResponseEnum.FORBIDDEN.getCode(), msg, args);
    }

    @Override
    public String toString() {
        return "ForbiddenException [message=" + getMessage() + ", code=" + getCode() + "]";
    }

}
