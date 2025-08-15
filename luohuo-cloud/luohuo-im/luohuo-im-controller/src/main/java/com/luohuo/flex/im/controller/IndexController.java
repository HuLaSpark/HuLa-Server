package com.luohuo.flex.im.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.R;
import com.luohuo.flex.model.vo.query.BindEmailReq;
import com.luohuo.flex.im.service.tenant.EmailService;

/**
 * system模块公开 服务
 *
 * @author 乾乾
 * @date 2025年06月05日00:50:36
 */
@Slf4j
@RestController
@RequestMapping("/anyTenant")
@AllArgsConstructor
@Tag(name = "邮箱服务")
public class IndexController {

	private final EmailService emailService;

	@PostMapping("/sendCode")
	@Operation(summary ="发送验证码到用户邮箱")
	public R<Boolean> sendCode(@Valid @RequestBody BindEmailReq req) {
		return R.success(emailService.sendVerificationCode(req));
	}
}
