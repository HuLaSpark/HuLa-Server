package com.hula.core.user.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.Claim;
import com.hula.common.constant.RedisKey;
import com.hula.common.event.TokenExpireEvent;
import com.hula.common.event.UserOfflineEvent;
import com.hula.core.user.domain.entity.User;
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

    /**
     * 校验token是不是有效
     */
    @Override
    public boolean verify(String token) {
        Long uid = JwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return false;
        }
        // 有可能token失效了，需要校验是不是和最新token一致
        return Objects.equals(token, RedisUtils.getStr(RedisKey.getKey(RedisKey.USER_TOKEN_FORMAT,
                JwtUtils.getLoginType(token), uid)));
    }

    @Override
    public LoginResultVO createToken(Long uid, String loginType) {
		// 1. uuid用于后续区分续签是给哪个token续签
		String uuid = UUID.randomUUID().toString(true);
        String tokenKey = RedisKey.getKey(RedisKey.USER_TOKEN_FORMAT, loginType, uid);
		String refreshTokenKey = RedisKey.getKey(RedisKey.USER_REFRESH_TOKEN_FORMAT, loginType, uid, uuid);
		String token = RedisUtils.getStr(tokenKey), refreshToken;
        if (StrUtil.isNotBlank(token)) {
            RedisUtils.del(tokenKey);
            // 1.2 token存在 旧设备下线
            applicationEventPublisher.publishEvent(new TokenExpireEvent(this, new OffLineResp(uid, loginType, RequestHolder.get().getIp())));
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
		// 1.校验续签token是否有效
		Map<String, Claim> verifyToken = JwtUtils.verifyToken(refreshTokenReq.getRefreshToken());

		// 2.判断redis里面是否存在续签token
		Long uid = verifyToken.get(JwtUtils.UID_CLAIM).asLong();
		String uuid = verifyToken.get(JwtUtils.UUID_CLAIM).asString();
		String type = verifyToken.get(JwtUtils.LOGIN_TYPE_CLAIM).asString();
		String token = RedisUtils.getStr(RedisKey.getKey(RedisKey.USER_REFRESH_TOKEN_FORMAT, type, uid, uuid));
		if(StrUtil.isEmpty(token)){
			throw TokenExceedException.expired();
		}

		// 3.生成新的token
		return createToken(uid, type);
	}

    @Override
    public void offline(User user) {
        applicationEventPublisher.publishEvent(new UserOfflineEvent(this,
                User.builder().id(RequestHolder.get().getUid()).lastOptTime(DateTime.now()).build()));
    }

}
