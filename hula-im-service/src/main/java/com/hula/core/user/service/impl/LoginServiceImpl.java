package com.hula.core.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hula.common.constant.RedisKey;
import com.hula.core.user.service.LoginService;
import com.hula.utils.JwtUtils;
import com.hula.utils.RedisUtils;
import com.hula.utils.RequestHolder;
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


    // token过期时间
    private static final Integer TOKEN_EXPIRE_DAYS = 5;
    // token续期时间
    private static final Integer TOKEN_RENEWAL_DAYS = 2;
    @Resource
    private JwtUtils jwtUtils;

    /**
     * 校验token是不是有效
     */
    @Override
    public boolean verify(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return false;
        }
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String realToken = RedisUtils.getStr(key);
        // 有可能token失效了，需要校验是不是和最新token一致
        return Objects.equals(token, realToken);
    }

    @Async
    @Override
    public void renewalTokenIfNecessary(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return;
        }
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        long expireDays = RedisUtils.getExpire(key, TimeUnit.DAYS);
        if (expireDays == -2) {//不存在的key
            return;
        }
        if (expireDays < TOKEN_RENEWAL_DAYS) {//小于一天的token帮忙续期
            RedisUtils.expire(key, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String token = RedisUtils.getStr(key);
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        //获取用户token
        token = jwtUtils.createToken(uid);
        RedisUtils.set(key, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);//token过期用redis中心化控制，初期采用5天过期，剩1天自动续期的方案。后续可以用双token实现
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        boolean verify = verify(token);
        return verify ? jwtUtils.getUidOrNull(token) : null;
    }

    @Override
    public void refreshToken() {
        RedisUtils.expire(RedisKey.getKey(RedisKey.USER_TOKEN_STRING, RequestHolder.get().getUid()), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    @Override
    public void logout() {
        RedisUtils.del(RedisKey.getKey(RedisKey.USER_TOKEN_STRING, RequestHolder.get().getUid()));
    }
}
