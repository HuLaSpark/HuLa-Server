package com.hula.core.user.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hula.common.constant.RedisKey;
import com.hula.common.enums.LoginTypeEnum;
import com.hula.common.event.UserOfflineEvent;
import com.hula.common.event.UserOnlineEvent;
import com.hula.common.event.UserRegisterEvent;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.req.user.LoginReq;
import com.hula.core.user.service.LoginService;
import com.hula.core.user.service.TokenService;
import com.hula.core.user.service.cache.UserCache;
import com.hula.utils.AssertUtil;
import com.hula.utils.JwtUtils;
import com.hula.utils.RedisUtils;
import com.hula.utils.RequestHolder;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
    private TokenService tokenService;
    @Resource
    private UserDao userDao;
    @Resource
    private UserCache userCache;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public String login(LoginReq loginReq) {
        User user = userDao.getOne(new QueryWrapper<User>().lambda()
                .eq(User::getAccount, loginReq.getAccount())
                .eq(User::getPassword, loginReq.getPassword()));
        AssertUtil.isNotEmpty(user, "账号或密码错误");
        // 上线通知
        if (!userCache.isOnline(user.getId())) {
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
        return  tokenService.createToken(user.getId(), LoginTypeEnum.PC);
    }

    @Override
    public String mobileLogin(LoginReq loginReq) {
        User user = userDao.getOne(new QueryWrapper<User>().lambda()
                .eq(User::getAccount, loginReq.getAccount())
                .eq(User::getPassword, loginReq.getPassword()));
        AssertUtil.isNotEmpty(user, "账号或密码错误");
        // 上线通知
        if (!userCache.isOnline(user.getId())) {
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
        return tokenService.createToken(user.getId(), LoginTypeEnum.MOBILE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(User user) {
        if (Objects.nonNull(user.getAccount())) {
            AssertUtil.isTrue(userDao.count(new QueryWrapper<User>().lambda()
                    .eq(User::getAccount, user.getAccount())) <= 0, "账号已注册");
        } else if (Objects.nonNull(user.getOpenId())) {
            AssertUtil.isTrue(userDao.count(new QueryWrapper<User>().lambda()
                    .eq(User::getOpenId, user.getOpenId())) <= 0, "微信号已绑定其他账号");
        }
        final User newUser = User.builder()
                .account(user.getAccount())
                .password(user.getPassword())
                .name(user.getName())
                .openId(user.getOpenId())
                .build();
        // 保存用户
        userDao.save(newUser);
        // 发布用户注册消息
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, newUser));
    }

    @Override
    public void logout() {
        RedisUtils.del(RedisKey.getKey(RedisKey.USER_TOKEN_FORMAT,
                JwtUtils.getLoginType(RequestHolder.get().getToken()),
                RequestHolder.get().getUid()));
        applicationEventPublisher.publishEvent(new UserOfflineEvent(this, User.builder()
                .id(RequestHolder.get().getUid()).lastOptTime(DateUtil.date()).build()));
    }

}
