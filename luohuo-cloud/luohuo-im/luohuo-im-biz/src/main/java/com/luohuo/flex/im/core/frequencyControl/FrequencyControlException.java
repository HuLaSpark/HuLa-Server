package com.luohuo.flex.im.core.frequencyControl;

import lombok.Data;
import java.io.Serial;

/**
 * 自定义限流异常
 * @author 乾乾
 */
@Data
public class FrequencyControlException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    public FrequencyControlException() {
        super();
    }

    public FrequencyControlException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }
}
