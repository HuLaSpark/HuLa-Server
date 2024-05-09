package com.hula.core.user.service;

/**
 * @author nyh
 */
public interface LoginService {

    /**
     * 校验token是不是有效
     */
    boolean verify(String token);

    /**
     * 刷新token有效期
     */
    void renewalTokenIfNecessary(String token);

    /**
     * 登录成功，获取token
     */
    String login(Long uid);

    /**
     * 如果token有效，返回uid
     */
    Long getValidUid(String token);
}
