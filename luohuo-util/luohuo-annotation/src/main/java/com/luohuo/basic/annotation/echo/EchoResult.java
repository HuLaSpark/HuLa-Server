package com.luohuo.basic.annotation.echo;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将该注解标记在service方法上， 调用该方法后，返回值中标记了@Echo 注解的字段将会自动注入属性
 * <p>
 * 注意，该方法不能写在 Mapper 的方法上。
 *
 * @author 乾乾
 * @date 2020年01月19日09:08:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface EchoResult {
    String[] ignoreFields() default {};
}
