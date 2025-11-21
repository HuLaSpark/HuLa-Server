package com.luohuo.basic.exception;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.utils.StrPool;

/**
 * 非运行期异常基类，所有自定义非运行时异常继承该类
 *
 * @author 乾乾
 * @version 1.0
 * @see RuntimeException
 */
public class BaseUncheckedException extends RuntimeException implements BaseException {

    private static final long serialVersionUID = -778887391066124051L;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 具体异常码
     */
    private int code;

    public BaseUncheckedException(Throwable cause) {
        super(cause);
    }

    public BaseUncheckedException(final int code, Throwable cause) {
        super(cause);
        this.code = code;
    }


    public BaseUncheckedException(final int code, final String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseUncheckedException(final int code, final String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public BaseUncheckedException(final int code, final String format, Object... args) {
        super(StrUtil.contains(format, StrPool.BRACE) ? StrUtil.format(format, args) : String.format(format, args));
        this.code = code;
        this.message = StrUtil.contains(format, StrPool.BRACE) ? StrUtil.format(format, args) : String.format(format, args);
    }


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return code;
    }
}
