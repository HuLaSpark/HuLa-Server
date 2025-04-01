package com.hula.ai.common.exception;

import com.hula.ai.common.enums.ResponseEnum;
import lombok.Data;

/**
 * 参数验证失败异常
 *
 * @author: 云裂痕
 * @date: 2020/3/4
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;

    public ValidateException() {
        this.code = ResponseEnum.BAD_REQUEST.getCode();
        this.msg = ResponseEnum.BAD_REQUEST.getMsg();
    }

    public ValidateException(String msg) {
        super(msg);
        this.code = ResponseEnum.BAD_REQUEST.getCode();
        this.msg = msg;
    }

}
