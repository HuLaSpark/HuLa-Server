package com.hula.core.user.service;

import com.hula.core.user.domain.vo.req.user.LoginReq;
import com.hula.core.user.domain.vo.req.user.RefreshTokenReq;
import com.hula.core.user.domain.vo.req.user.RegisterReq;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author nyh
 */
public interface LoginService {

    /**
     * 登录成功，获取token
     */
	LoginResultVO login(LoginReq loginReq, HttpServletRequest request);

	/**
	 * 刷新token
	 */
	LoginResultVO refreshToken(RefreshTokenReq refreshTokenReq);

    /**
     * 普通注册
     *
     * @param req 用户注册信息
     */
    void normalRegister(RegisterReq req);

    /**
     * 微信注册
     *
     * @param req 用户基础信息 + 微信的 openId
     */
    void wxRegister(RegisterReq req);

    /**
     * 退出登录
     */
    void logout(Boolean autoLogin);
}
