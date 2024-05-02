package com.hula.core.user.service.impl;

import com.hula.common.constant.RedisKey;
import com.hula.core.user.service.LoginService;
import com.hula.common.utils.JwtUtils;
import com.hula.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author nyh
 */
@Service
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEWAL_DAYS = 1;
    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean verify(String token) {
        return false;
    }

    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return;
        }
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        Long expireDays = RedisUtils.getExpire(key, TimeUnit.DAYS);
        if (expireDays == -2) {
            return;
        }
        if (expireDays < TOKEN_RENEWAL_DAYS) {
            RedisUtils.expire(key, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        RedisUtils.set(key, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return null;
        }
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String oldToken = RedisUtils.getStr(key);
        return Objects.equals(oldToken, token) ? uid : null;
    }
}
