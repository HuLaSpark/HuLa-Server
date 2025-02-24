package com.hula.core.user.service;

import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author nyh
 */
public interface LoginService {

    /**
     * 登录成功，获取token
     */
	LoginResultVO login(User user, HttpServletRequest request);

    /**
     * @param user 用户
     * @return {@link String } token
     */
	LoginResultVO mobileLogin(User user, HttpServletRequest request);

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
