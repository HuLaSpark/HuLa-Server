package com.hula.core.user.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hula.common.constant.RedisKey;
import com.hula.common.enums.LoginTypeEnum;
import com.hula.common.event.UserOfflineEvent;
import com.hula.common.event.UserOnlineEvent;
import com.hula.common.event.UserRegisterEvent;
import com.hula.core.chat.service.ContactService;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
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

/**
 * @author nyh
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private TokenService tokenService;
    @Resource
    private UserDao userDao;
    @Resource
    private ContactService contactService;
    @Resource
    private UserCache userCache;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public String login(User user) {
        User queryUser = userDao.getOne(new QueryWrapper<User>().lambda()
                .eq(User::getAccount, user.getAccount()));
        AssertUtil.isNotEmpty(queryUser, "账号或密码错误");
        AssertUtil.equal(queryUser.getPassword(), user.getPassword(), "账号或密码错误");
        // 上线通知
        if (!userCache.isOnline(queryUser.getId())) {
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, queryUser));
        }
        return tokenService.createToken(queryUser.getId(), LoginTypeEnum.PC);
    }

    @Override
    public String mobileLogin(User user) {
        User queryUser = userDao.getOne(new QueryWrapper<User>().lambda()
                .eq(User::getAccount, user.getAccount()));
        AssertUtil.isNotEmpty(queryUser, "账号或密码错误");
        AssertUtil.equal(queryUser.getPassword(), user.getPassword(), "账号或密码错误");
        // 上线通知
        if (!userCache.isOnline(queryUser.getId())) {
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, queryUser));
        }
        return tokenService.createToken(queryUser.getId(), LoginTypeEnum.MOBILE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void normalRegister(User user) {
        AssertUtil.isTrue(userDao.count(new QueryWrapper<User>().lambda()
                    .eq(User::getAccount, user.getAccount())) <= 0, "账号已注册");
        final User newUser = User.builder()
                .avatar(user.getAvatar())
                .account(user.getAccount())
                .password(user.getPassword())
                .name(user.getName())
                .openId(user.getOpenId())
                .build();
        // 保存用户
        userDao.save(newUser);
        // 创建会话
        contactService.createContact(newUser.getId(), 1L);
        // 发布用户注册消息
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, newUser));
    }

    @Override
    public void wxRegister(User user) {
        AssertUtil.isNotEmpty(user.getOpenId(), "未找到openid");
        AssertUtil.isTrue(userDao.count(new QueryWrapper<User>().lambda()
                .eq(User::getOpenId, user.getOpenId())) <= 0, "微信号已绑定其他账号");

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
