package com.hula.core.user.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.Claim;
import com.hula.common.constant.RedisKey;
import com.hula.common.event.TokenExpireEvent;
import com.hula.common.event.UserOfflineEvent;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;
import com.hula.core.user.service.TokenService;
import com.hula.utils.JwtUtils;
import com.hula.utils.RedisUtils;
import com.hula.utils.RequestHolder;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

/**
 * @author nyh
 */
@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

    // token 过期时间
    private static final Integer TOKEN_EXPIRE_DAYS = 5;
    // refreshToken 过期时间
    private static final Integer TOKEN_RENEWAL_DAYS = 30;

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
        String tokenKey = RedisKey.getKey(RedisKey.USER_TOKEN_FORMAT, loginType, uid);
		String refreshTokenKey = RedisKey.getKey(RedisKey.USER_REFRESH_TOKEN_FORMAT, loginType, uid);
		String token = RedisUtils.getStr(tokenKey), refreshToken;
        if (StrUtil.isNotBlank(token)) {
            RedisUtils.del(tokenKey);
            User user = User.builder().id(uid).build();
            user.refreshIp(RequestHolder.get().getIp());
            // 旧设备下线
            applicationEventPublisher.publishEvent(new TokenExpireEvent(this, user));
        }
        // 创建用户token
        token = JwtUtils.createToken(uid, loginType, TOKEN_EXPIRE_DAYS);
		refreshToken = JwtUtils.createToken(uid, loginType, TOKEN_RENEWAL_DAYS);

		// 刷新存放时间
		RedisUtils.set(tokenKey, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
		RedisUtils.set(refreshTokenKey, refreshToken, TOKEN_RENEWAL_DAYS, TimeUnit.DAYS);
		return new LoginResultVO(token, refreshToken, LocalDateTime.now().plusDays(TOKEN_EXPIRE_DAYS), loginType);
    }

    @Override
    public LoginResultVO refreshToken() {
		Map<String, Claim> verifyToken = JwtUtils.verifyToken(RequestHolder.get().getToken());

		Long uid = verifyToken.get(JwtUtils.UID_CLAIM).asLong();
		String type = verifyToken.get(JwtUtils.LOGIN_TYPE_CLAIM).asString();
		return createToken(uid, type);
	}

    @Override
    public void offline(User user) {
        applicationEventPublisher.publishEvent(new UserOfflineEvent(this,
                User.builder().id(RequestHolder.get().getUid()).lastOptTime(DateTime.now()).build()));
    }

}
