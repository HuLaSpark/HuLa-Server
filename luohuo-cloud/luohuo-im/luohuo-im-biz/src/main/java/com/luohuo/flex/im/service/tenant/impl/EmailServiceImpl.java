package com.luohuo.flex.im.service.tenant.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.common.cache.common.ConfigCacheKeyBuilder;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.common.cache.common.CaptchaCacheKeyBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.luohuo.flex.common.utils.ToolsUtil;
import com.luohuo.flex.model.vo.query.BindEmailReq;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.service.tenant.EmailService;

import java.time.LocalDateTime;

/**
 * 邮箱服务 TODO 整合实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender mailSender;
	private final UserDao userDao;
	private final CachePlusOps cachePlusOps;

	@Value("${spring.mail.username}")
	private String fromEmail;

	/**
	 * 发送验证码到用户邮件
	 * @param req 邮箱参数
	 */
	public Boolean sendVerificationCode(BindEmailReq req) {
		// 1. 校验数字验证码是否正确
		CacheResult<String> cacheResult = cachePlusOps.hGet(CaptchaCacheKeyBuilder.hashBuild("numberCode", req.getUuid()));
		String code = cacheResult.getRawValue();
		if(StrUtil.isEmpty(code) || !code.equals(req.getCode())){
			throw new BizException("验证码输入错误!");
		}

		// 2. 校验邮箱是否存在
		if ("register".equals(req.getOperationType()) && userDao.existsByEmailAndIdNot(null, req.getEmail())) {
			throw new BizException("该邮箱已被其他账号绑定");
		}

		try{
			long expireTime = 1800;
			// 3. 发送邮箱验证码到用户邮箱
			cachePlusOps.get(CaptchaCacheKeyBuilder.hashBuild("numberCode", req.getUuid()));
			String time = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");

			// 缓存重写
			CacheResult<String> result = cachePlusOps.hGet(ConfigCacheKeyBuilder.build("systemName"));
			String systemName = result.getValue();

			result = cachePlusOps.hGet(ConfigCacheKeyBuilder.build("codeHtmlTemplate"));
			String htmlTemplate = result.getValue();

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(req.getEmail());
			helper.setSubject("验证码邮件");

			// 替换占位符并设置 HTML 内容
			String emailCode = ToolsUtil.randomCount(100000, 999999).toString();
			String htmlContent = StrUtil.format(htmlTemplate, systemName, systemName, systemName, emailCode, expireTime / 60,fromEmail, systemName, time);
			helper.setText(htmlContent, true);

			log.info("验证码: {}", emailCode);
			mailSender.send(message);
			cachePlusOps.hSet(CaptchaCacheKeyBuilder.hashBuild("emailCode", req.getUuid(), expireTime), emailCode);
		} catch (MessagingException e) {
			throw new BizException("邮箱发送失败!");
		}

		// 5. 移除验证码
		cachePlusOps.hDel("numberCode", req.getUuid());
		return true;
	}
}