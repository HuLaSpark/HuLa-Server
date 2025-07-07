package com.hula.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author nyh
 */

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum {

    PARAM_VALID(-2, "参数校验失败{0}"),
    ;
    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
