package com.hula.core.user.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hula.common.annotation.RedissonLock;
import com.hula.common.constant.RedisKey;
import com.hula.common.event.UserOfflineEvent;
import com.hula.common.event.UserOnlineEvent;
import com.hula.common.event.UserRegisterEvent;
import com.hula.common.utils.IPUtils;
import com.hula.core.chat.service.ContactService;
import com.hula.core.chat.service.RoomService;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.UserTypeEnum;
import com.hula.core.user.domain.vo.req.user.ForgotPasswordReq;
import com.hula.core.user.domain.vo.req.user.LoginReq;
import com.hula.core.user.domain.vo.req.user.RefreshTokenReq;
import com.hula.core.user.domain.vo.req.user.RegisterReq;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;
import com.hula.core.user.service.FriendService;
import com.hula.core.user.service.LoginService;
import com.hula.core.user.service.TokenService;
import com.hula.core.user.service.cache.UserCache;
import com.hula.exception.BizException;
import com.hula.exception.TokenExceedException;
import com.hula.snowflake.uid.UidGenerator;
import com.hula.snowflake.uid.utils.Base62Encoder;
import com.hula.utils.AssertUtil;
import com.hula.utils.JwtUtils;
import com.hula.utils.RedisUtils;
import com.hula.utils.RequestHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

/**
 * @author nyh
 */
@Service
public class LoginServiceImpl implements LoginService {
	@Resource
	private UidGenerator uidGenerator;
    @Resource
    private TokenService tokenService;
    @Resource
    private UserDao userDao;
	@Resource
	private RoomService roomService;
    @Resource
    private ContactService contactService;
    @Resource
    private UserCache userCache;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
	@Resource
	private FriendService friendService;

    @Override
    public LoginResultVO login(LoginReq loginReq, HttpServletRequest request) {
		User queryUser = userDao.getOne(new QueryWrapper<User>().lambda()
				.and(w -> w.eq(User::getEmail, loginReq.getAccount()).or().eq(User::getAccount, loginReq.getAccount()))
				.eq(User::getUserType, UserTypeEnum.NORMAL.getValue())
		);

        AssertUtil.isNotEmpty(queryUser, "账号或密码错误");
        AssertUtil.equal(queryUser.getPassword(), loginReq.getPassword(), "账号或密码错误");
        // 上线通知
        if (!userCache.isOnline(queryUser.getId())) {
			queryUser.refreshIp(IPUtils.getHostIp(request));
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, queryUser));
        }
        return tokenService.createToken(queryUser.getId(), loginReq.getSource());
    }

	@Override
	public LoginResultVO refreshToken(RefreshTokenReq refreshTokenReq) {
		return tokenService.refreshToken(refreshTokenReq);
	}

	@Override
    @Transactional(rollbackFor = Exception.class)
	@RedissonLock(key = "#req.email")
    public void normalRegister(RegisterReq req) {
		checkCode(req.getUuid(), req.getCode());

		// 2. 检查邮箱是否已被其他用户绑定
		if (userDao.existsByEmailAndIdNot(null, req.getEmail())) {
			throw new BizException("该邮箱已被其他账号绑定");
		}

		String account = req.getEmail().split("@")[0];
		boolean exists = userDao.count(new QueryWrapper<User>().lambda().eq(User::getAccount, account)) > 0;

		// 3. 走注册流程
        final User newUser = User.builder()
                .avatar(req.getAvatar())
                .account(exists? req.getEmail(): account)
				.email(req.getEmail())
                .password(req.getPassword())
                .name(req.getName())
                .openId(req.getOpenId())
				.userType(UserTypeEnum.NORMAL.getValue())
                .build();
        // 保存用户
        userDao.save(newUser);
        // 创建会话
        contactService.createContact(newUser.getId(), 1L);
		// 创建群成员
		roomService.createGroupMember(1L, newUser.getId());
		// 与系统用户创建好友关系
		friendService.createSystemFriend(Arrays.asList(newUser.getId()));
        // 发布用户注册消息
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, newUser));
		// 移除验证码
		RedisUtils.hdel("emailCode", req.getUuid());
    }

    @Override
    public void wxRegister(RegisterReq req) {
        AssertUtil.isNotEmpty(req.getOpenId(), "未找到openid");
        AssertUtil.isTrue(userDao.count(new QueryWrapper<User>().lambda()
                .eq(User::getOpenId, req.getOpenId())) <= 0, "微信号已绑定其他账号");

        final User newUser = User.builder()
				.account(Base62Encoder.createAccount(uidGenerator.getUid()))
                .email(req.getEmail())
                .password(req.getPassword())
                .name(req.getName())
                .openId(req.getOpenId())
                .build();
        // 保存用户
        userDao.save(newUser);
        // 发布用户注册消息
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, newUser));
    }

    @Override
    public void logout(Boolean autoLogin) {
		// 1. 拿到token
		String token = RequestHolder.get().getToken();

		try {
			// 2. 解析token里面的数据，精准拿到当前用户的refreshToken
			Map<String, Claim> verifyToken = JwtUtils.verifyToken(token);
			Long uid = verifyToken.get(JwtUtils.UID_CLAIM).asLong();
			String type = verifyToken.get(JwtUtils.LOGIN_TYPE_CLAIM).asString();
			String uuid = verifyToken.get(JwtUtils.UUID_CLAIM).asString();

			if(!autoLogin){
				RedisUtils.del(RedisKey.getKey(RedisKey.USER_REFRESH_TOKEN_FORMAT, type, uid, uuid));
			}

			// 3. 删除token
			RedisUtils.del(RedisKey.getKey(RedisKey.USER_TOKEN_FORMAT, type, uid, uuid));
			applicationEventPublisher.publishEvent(new UserOfflineEvent(this, User.builder().id(RequestHolder.get().getUid()).lastOptTime(DateUtil.date()).build()));
		} catch (Exception e) {
			throw TokenExceedException.expired();
		}
	}

	@Override
	public Boolean forgotPassword(ForgotPasswordReq req) {
		// 1. 校验
		checkCode(req.getUuid(), req.getCode());

		// 2. 修改密码
		User dbuUser = userDao.getByEmail(req.getEmail());
		User user = new User();
		user.setId(dbuUser.getId());
		user.setPassword(req.getPassword());
		return userDao.updateById(user);
	}

	/**
	 * 检查验证码
	 * @param uuid 验证码id
	 * @param code 验证码值
	 */
	private static void checkCode(String uuid, String code) {
		String emailCode;
		Object codeObj = RedisUtils.hget("emailCode", uuid);
		if (ObjectUtil.isNotNull(codeObj)) {
			emailCode = codeObj.toString();
		} else {
			throw new BizException("验证码已过期");
		}

		if (StrUtil.isEmpty(emailCode) || !emailCode.equals(code)) {
			throw new BizException("验证码错误!");
		}
	}

}
