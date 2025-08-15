package com.luohuo.basic.exception;

import com.luohuo.basic.exception.code.ResponseEnum;

/**
 * token过期
 *
 * @author 乾乾
 */
public class TokenExceedException extends BaseUncheckedException {

    private static final long serialVersionUID = 1L;

    public TokenExceedException(int code, String message) {
        super(code, message);
    }

	public static TokenExceedException expired() {
		return new TokenExceedException(ResponseEnum.UNAUTHORIZED.getCode(), ResponseEnum.UNAUTHORIZED.getMsg());
	}

    public static TokenExceedException wrap() {
        return new TokenExceedException(ResponseEnum.JWT_TOKEN_EXCEED.getCode(), ResponseEnum.JWT_TOKEN_EXCEED.getMsg());
    }

    @Override
    public String toString() {
        return "TokenExceedException [message=" + getMessage() + ", code=" + getCode() + "]";
    }

}
