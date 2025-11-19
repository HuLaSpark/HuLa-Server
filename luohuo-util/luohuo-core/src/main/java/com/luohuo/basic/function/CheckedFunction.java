package com.luohuo.basic.function;

/**
 * 处理异常的 函数
 *
 * @author 乾乾
 * @date 2019/05/15
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    /**
     * 执行
     *
     * @param t 入参
     * @return R 出参
     * @throws Exception 异常
     */
    R apply(T t) throws Exception;


}
