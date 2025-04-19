package com.hula.core.user.controller;

import com.hula.core.user.domain.vo.req.user.ForgotPasswordReq;
import com.hula.core.user.domain.vo.req.user.LoginReq;
import com.hula.core.user.domain.vo.req.user.LogoutReq;
import com.hula.core.user.domain.vo.req.user.RefreshTokenReq;
import com.hula.core.user.domain.vo.req.user.RegisterReq;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;
import com.hula.core.user.service.LoginService;
import com.hula.domain.vo.res.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * token
 * @author ZOL
 */
@RestController
@RequestMapping("/token")
@Tag(name = "用户管理")
public class TokenController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    @Operation(summary ="用户登录")
    public ApiResult<LoginResultVO> login(@Valid @RequestBody LoginReq loginReq, HttpServletRequest request) {
        return ApiResult.success(loginService.login(loginReq, request));
    }

    @PostMapping("/logout")
    @Operation(summary ="用户登出")
    public ApiResult<Boolean> logout(@Valid @RequestBody LogoutReq logoutReq) {
        loginService.logout(logoutReq.getAutoLogin());
        return ApiResult.success(Boolean.TRUE);
    }

    @PostMapping("/register")
    @Operation(summary ="用户注册")
    public ApiResult<String> register(@Valid @RequestBody RegisterReq registerReq) {
        loginService.normalRegister(registerReq);
        return ApiResult.success(registerReq.getEmail());
    }

	@PostMapping("/forgotPassword")
	@Operation(summary ="忘记密码|重置密码")
	public ApiResult<Boolean> forgotPassword(@Valid @RequestBody ForgotPasswordReq forgotPasswordReq) {
		return ApiResult.success(loginService.forgotPassword(forgotPasswordReq));
	}

    @PostMapping("/refreshToken")
    @Operation(summary ="token续签")
    public ApiResult<LoginResultVO> refreshToken(@RequestBody RefreshTokenReq refreshTokenReq) {
        return ApiResult.success(loginService.refreshToken(refreshTokenReq));
    }
}

