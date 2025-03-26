package com.hula.core.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hula.common.utils.ToolsUtil;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.vo.req.user.BindEmailReq;
import com.hula.core.user.service.EmailService;
import com.hula.exception.BizException;
import com.hula.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮箱服务
 */
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

	@Autowired
	private UserDao userDao;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

	/**
	 * 发送验证码到用户邮件
	 * @param req 邮箱参数
	 */
	public Boolean sendVerificationCode(BindEmailReq req) {
		// 1. 校验数字验证码是否正确
		String code = RedisUtils.hget("numberCode", req.getUuid()).toString();
		if(StrUtil.isEmpty(code) || !code.equals(req.getCode())){
			throw new BizException("验证码输入错误!");
		}

		// 2. 校验邮箱是否存在
		if (userDao.existsByEmailAndIdNot(null, req.getEmail())) {
			throw new BizException("该邮箱已被其他账号绑定");
		}

		// 3. 发送邮箱验证码到用户邮箱
		String emailCode = ToolsUtil.randomCount(100000, 999999).toString();
		RedisUtils.hset("emailCode", req.getUuid(), emailCode, 300);

		SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(req.getEmail());
        message.setSubject("邮箱绑定");
        message.setText("您的验证码是：" + emailCode + "，5分钟内有效。");
        mailSender.send(message);

		// 5. 移除验证码
		RedisUtils.hdel("numberCode", req.getUuid());
		return true;
    }
}