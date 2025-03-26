package com.hula.core.user.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.vo.req.user.BindEmailReq;
import com.hula.core.user.service.ConfigService;
import com.hula.core.user.service.EmailService;
import com.hula.exception.BizException;
import com.hula.utils.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 邮箱服务
 */
@Service
public class EmailServiceImpl implements EmailService {
	@Resource
	private JavaMailSender mailSender;

	@Resource
	private UserDao userDao;

	@Resource
	private ConfigService configService;

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
		String systemName = configService.get("systemName");
		String time = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");

		String htmlTemplate = configService.get("codeHtmlTemplate");
		try{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(req.getEmail());
			helper.setSubject("验证码邮件");

			// 替换占位符并设置 HTML 内容
			String htmlContent = StrUtil.format(htmlTemplate, systemName, "注册验证码", systemName, systemName, code, fromEmail, systemName, time);
			helper.setText(htmlContent, true);

			mailSender.send(message);
		} catch (MessagingException e) {
			throw new BizException("邮箱发送失败!");
		}

		// 5. 移除验证码
		RedisUtils.hdel("numberCode", req.getUuid());
		return true;
	}
}