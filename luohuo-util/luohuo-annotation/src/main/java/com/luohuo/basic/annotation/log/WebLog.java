package com.luohuo.basic.annotation.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 *
 * @author 乾乾
 * @date 2019/2/1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebLog {
    /**
     * 是否启用 操作日志
     * 禁用控制优先级：luohuo.log.enabled = false > 控制器类上@WebLog(enabled = false) > 控制器方法上@WebLog(enabled = false)
     *
     * @return 是否启用
     */
    boolean enabled() default true;

    /**
     * 操作日志的描述， 支持spring 的 SpEL 表达式。
     *
     * @return {String}
     */
    String value() default "";

    /**
     * 模块
     */
    String modular() default "";

    /**
     * 是否拼接Controller类上@Api注解的描述值
     *
     * @return 是否拼接Controller类上的描述值
     */
    boolean controllerApiValue() default true;

    /**
     * 是否记录方法的入参
     *
     * @return 是否记录方法的入参
     */
    boolean request() default true;

    /**
     * 若设置了 request = false、requestByError = true，则方法报错时，依然记录请求的入参
     *
     * @return 当 request = false时， 方法报错记录请求参数
     */
    boolean requestByError() default true;

    /**
     * 是否记录返回值
     *
     * @return 是否记录返回值
     */
    boolean response() default true;
}
