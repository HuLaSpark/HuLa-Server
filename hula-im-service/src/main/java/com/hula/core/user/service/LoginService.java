package com.hula.core.user.service;

import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.req.user.LoginReq;

/**
 * @author nyh
 */
public interface LoginService {

    /**
     * 登录成功，获取token
     */
    String login(LoginReq loginReq);

    /**
     * @param loginReq 用户
     * @return {@link String } token
     */
    String mobileLogin(LoginReq loginReq);

    void register(User user);

    /**
     * 退出登录
     */
    void logout();


}
