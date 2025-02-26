package com.hula.exception;

import com.hula.enums.HttpErrorEnum;
import lombok.Data;

/**
 * token过期
 *
 * @author 乾乾
 */
@Data
public class TokenExceedException extends RuntimeException {

	/**
	 * 异常信息
	 */
	private String message;

	/**
	 * 具体异常码
	 */
	private int code;

    private static final long serialVersionUID = 1L;

    public TokenExceedException(int code, String message) {
		this.code = code;
		this.message = message;
    }

	public static TokenExceedException expired() {
		return new TokenExceedException(HttpErrorEnum.ACCESS_DENIED.getCode(), HttpErrorEnum.ACCESS_DENIED.getMsg());
	}

    public static TokenExceedException wrap() {
        return new TokenExceedException(HttpErrorEnum.JWT_TOKEN_EXCEED.getCode(), HttpErrorEnum.JWT_TOKEN_EXCEED.getMsg());
    }

    @Override
    public String toString() {
        return "TokenExceedException [message=" + getMessage() + ", code=" + getCode() + "]";
    }

}
