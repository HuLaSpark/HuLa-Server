package com.hula.core.user.service;

import com.hula.core.user.domain.entity.User;

/**
 * @author nyh
 */
public interface LoginService {

    /**
     * 登录成功，获取token
     */
    String login(User user);

    /**
     * @param user 用户
     * @return {@link String } token
     */
    String mobileLogin(User user);

    /**
     * 普通注册
     *
     * @param user 用户
     */
    void normalRegister(User user);

    /**
     * 微信注册
     *
     * @param user 用户
     */
    void wxRegister(User user);

    /**
     * 退出登录
     */
    void logout();


}
