package com.hula.core.user.controller;

import com.hula.core.user.domain.vo.req.user.BindEmailReq;
import com.hula.core.user.service.CaptchaService;
import com.hula.core.user.service.EmailService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 验证码服务
 *
 * @author resonance
 * @version v1.0
 * @date 2025/03/24 5:20 PM
 * @create 乾乾
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
@AllArgsConstructor
@Tag(name = "验证码")
public class CaptchaController {

    private final CaptchaService captchaService;

	private final EmailService emailService;

    @Operation(summary = "获取图片验证码", description = "获取图片验证码")
    @Parameters({@Parameter(description = "唯一字符串: 前端随机生成一个唯一字符串用于生成验证码，并将key传给后台用于验证")})
    @GetMapping(value = "/captcha")
    public ApiResult<HashMap<String, Object>> captcha() {
        return ApiResult.success(captchaService.createImg());
    }

	@PostMapping("/sendCode")
	@Operation(summary ="发送验证码到用户邮箱")
	public ApiResult<Boolean> sendCode(@Valid @RequestBody BindEmailReq req) {
		return ApiResult.success(emailService.sendVerificationCode(req));
	}
}
