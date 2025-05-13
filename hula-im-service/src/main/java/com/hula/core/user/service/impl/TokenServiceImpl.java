package com.hula.core.user.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.Claim;
import com.hula.common.constant.RedisKey;
import com.hula.common.event.TokenExpireEvent;
import com.hula.core.user.domain.vo.req.user.RefreshTokenReq;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;
import com.hula.core.user.domain.vo.resp.user.OffLineResp;
import com.hula.core.user.service.TokenService;
import com.hula.exception.TokenExceedException;
import com.hula.utils.JwtUtils;
import com.hula.utils.RedisUtils;
import com.hula.utils.RequestHolder;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author nyh
 */
@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

	private static final Integer DAY = 60 * 60 * 24;

    // token 过期时间
	private static final Integer TOKEN_EXPIRE_DAYS = 30 * DAY;
    // refreshToken 过期时间
    private static final Integer TOKEN_RENEWAL_DAYS = 365 * DAY;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public boolean verify(String token) {
		Map<String, Claim> claim = JwtUtils.verifyToken(token);
		if (Objects.isNull(claim)) {
			return false;
		}
		Long uid = claim.get(JwtUtils.UID_CLAIM).asLong();
		String loginType = claim.get(JwtUtils.LOGIN_TYPE_CLAIM).asString();
		String uuid = claim.get(JwtUtils.UUID_CLAIM).asString();
        return Objects.equals(token, RedisUtils.getStr(RedisKey.getKey(RedisKey.USER_TOKEN_FORMAT, loginType, uid, uuid)));
    }

    @Override
    public LoginResultVO createToken(Long uid, String loginType) {
		// 1. uuid用于后续区分续签是给哪个token续签
		String uuid = UUID.randomUUID().toString(true);
        String tokenKey = RedisKey.getKey(RedisKey.USER_TOKEN_FORMAT, loginType, uid, uuid);
		String refreshTokenKey = RedisKey.getKey(RedisKey.USER_REFRESH_TOKEN_FORMAT, loginType, uid, uuid);
		String token = RedisUtils.getStr(tokenKey), refreshToken;
		String key = RedisKey.getKey(RedisKey.USER_REFRESH_TOKEN_UID_FORMAT, loginType, uid);
		RedisUtils.del(tokenKey, key);

		// 1.2 token存在 旧设备下线
        if (StrUtil.isNotBlank(token)) {
            applicationEventPublisher.publishEvent(new TokenExpireEvent(this, new OffLineResp(uid, loginType, RequestHolder.get().getIp(), uuid)));
        }

        // 2. 创建用户token
		token = JwtUtils.createToken(uid, loginType, uuid, TOKEN_EXPIRE_DAYS);
		refreshToken = JwtUtils.createToken(uid, loginType, uuid, TOKEN_RENEWAL_DAYS);

		// 3. 刷新存放时间
		RedisUtils.set(tokenKey, token, TOKEN_EXPIRE_DAYS, TimeUnit.SECONDS);
		RedisUtils.set(refreshTokenKey, refreshToken, TOKEN_RENEWAL_DAYS, TimeUnit.SECONDS);
		return new LoginResultVO(token, refreshToken, loginType);
    }

    @Override
    public LoginResultVO refreshToken(RefreshTokenReq refreshTokenReq) {
		Map<String, Claim> verifyToken;
		try {
			// 1.校验续签token是否有效
			verifyToken = JwtUtils.verifyToken(refreshTokenReq.getRefreshToken());

			// 2.判断redis里面是否存在续签token
			Long uid = verifyToken.get(JwtUtils.UID_CLAIM).asLong();
			String type = verifyToken.get(JwtUtils.LOGIN_TYPE_CLAIM).asString();
			String key = RedisKey.getKey(RedisKey.USER_REFRESH_TOKEN_FORMAT, type, uid, verifyToken.get(JwtUtils.UUID_CLAIM).asString());

			String token = RedisUtils.getStr(key);
			if(StrUtil.isEmpty(token)){
				throw TokenExceedException.expired();
			}

			// 3.生成新的token
			RedisUtils.del(key);
			return createToken(uid, type);
		} catch (Exception e) {
			throw TokenExceedException.expired();
		}
	}
}
