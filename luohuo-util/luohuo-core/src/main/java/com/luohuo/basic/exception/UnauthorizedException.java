package com.luohuo.basic.exception;


import com.luohuo.basic.exception.code.ResponseEnum;

/**
 * 401 未认证 未登录
 *
 * @author 乾乾
 * @version 1.0
 */
public class UnauthorizedException extends BaseUncheckedException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(int code, String message) {
        super(code, message);
    }

    public UnauthorizedException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public static UnauthorizedException wrap(String msg) {
        return new UnauthorizedException(ResponseEnum.UNAUTHORIZED.getCode(), msg);
    }

    public static UnauthorizedException wrap(ResponseEnum ec) {
        return new UnauthorizedException(ec.getCode(), ec.getMsg());
    }

    @Override
    public String toString() {
        return "UnauthorizedException [message=" + getMessage() + ", code=" + getCode() + "]";
    }

}
