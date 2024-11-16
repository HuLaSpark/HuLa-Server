package com.hula.exception;

import com.hula.enums.ErrorEnum;
import lombok.Data;

import java.io.Serial;

/**
 * 自定义限流异常
 * @author nyh
 */
@Data
public class FrequencyControlException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *  错误码
     */
    protected Integer errorCode;

    /**
     *  错误信息
     */
    protected String errorMsg;

    public FrequencyControlException() {
        super();
    }

    public FrequencyControlException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public FrequencyControlException(ErrorEnum error) {
        super(error.getErrorMsg());
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
    }
}
