package com.hula.core.user.service;

import com.hula.core.user.domain.vo.req.user.RefreshTokenReq;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;

/**
 * @author nyh
 */
public interface TokenService {

    /**
     * 校验token是不是有效
     */
    boolean verify(String token);

    /**
     * @param uid 用户id
     * @param loginTypeEnum 登录类型
     * @return {@link String } 令牌
     */
	LoginResultVO createToken(Long uid, String loginTypeEnum);

    /**
     * token续签
     */
	LoginResultVO refreshToken(RefreshTokenReq refreshTokenReq);
}
