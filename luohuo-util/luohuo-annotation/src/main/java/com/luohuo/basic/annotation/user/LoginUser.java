package com.luohuo.basic.annotation.user;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求的方法参数SysUser上添加该注解，则注入当前登录人信息
 * 例1：public void test(@LoginUser SysUser user) // 取BaseContextHandler中的 用户id、账号、姓名、组织id、岗位id等信息
 * 例2：public void test(@LoginUser(isRoles = true) SysUser user) //能获取SysUser对象的实时的用户信息和角色信息
 * 例3：public void test(@LoginUser(isOrg = true) SysUser user) //能获取SysUser对象的实时的用户信息和组织信息
 * 例4：public void test(@LoginUser(isStation = true) SysUser user) //能获取SysUser对象的实时的用户信息和岗位信息
 * 例5：public void test(@LoginUser(isFull = true) SysUser user) //能获取SysUser对象的所有信息
 * 例6：public void test(@LoginUser(isResource = true) SysUser user) //能获取SysUser对象的实时的用户信息和资源信息
 * <p>
 * 缺点1： 不能用于 BaseController 类中的方法
 *
 * @author 乾乾
 * @date 2018/7/24 16:44
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
    /**
     * 是否查询SysUser对象所有信息，true则通过rpc接口查询
     */
    boolean isFull() default false;

    /**
     * 是否只查询员工信息，true则通过rpc接口查询
     */
    boolean isEmployee() default false;

    /**
     * 是否只查询用户信息，true则通过rpc接口查询
     */
    boolean isUser() default false;

    /**
     * 是否只查询角色信息，true则通过rpc接口查询
     */
    boolean isRoles() default false;


    /**
     * 是否只查询 资源 信息，true则通过rpc接口查询
     */
    boolean isResource() default false;

    /**
     * 是否只查询组织信息，true则通过rpc接口查询
     */
    boolean isOrg() default false;

    /**
     * 是否只查询主组织信息，true则通过rpc接口查询
     */
    boolean isMainOrg() default false;

    /**
     * 是否只查询岗位信息，true则通过rpc接口查询
     */
    boolean isPosition() default false;
}
