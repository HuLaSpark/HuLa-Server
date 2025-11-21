package com.luohuo.basic.exception.code;

/**
 * 异常编码
 *
 * @author 乾乾
 * @date 2017-12-25 13:46
 */
public interface BaseExceptionCode {
    /**
     * 异常编码
     *
     * @return 异常编码
     */
    int getCode();

    /**
     * 异常消息
     *
     * @return 异常消息
     */
    String getMsg();
}
