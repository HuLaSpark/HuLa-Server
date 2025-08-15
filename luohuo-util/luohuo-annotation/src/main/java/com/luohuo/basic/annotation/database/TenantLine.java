package com.luohuo.basic.annotation.database;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/8/24 9:19 PM
 * @create [2022/8/24 9:19 PM ] [tangyh] [初始创建]
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TenantLine {
    boolean value() default true;
}
