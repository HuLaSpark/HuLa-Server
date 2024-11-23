package com.hula.core.user.service;

import com.hula.common.enums.LoginTypeEnum;

/**
 * @author nyh
 */
public interface TokenService {

    /**
     * 校验token是不是有效
     */
    boolean verify(String token);

    /**
     * 刷新token有效期
     */
    void renewalTokenIfNecessary(String token);

    /**
     * @param uid 用户id
     * @param loginTypeEnum 登录类型
     * @return {@link String } 令牌
     */
    String createToken(Long uid, LoginTypeEnum loginTypeEnum);

    /**
     * 延长token有效期
     */
    void refreshToken();


}
