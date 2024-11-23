package com.hula.core.user.controller;


import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.req.user.LoginReq;
import com.hula.core.user.domain.vo.req.user.RegisterReq;
import com.hula.core.user.service.LoginService;
import com.hula.core.user.service.TokenService;
import com.hula.domain.vo.res.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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

    @Resource
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary ="用户登录")
    public ApiResult<String> login(@Valid @RequestBody LoginReq loginReq) {
        String token = loginService.login(loginReq);
        return ApiResult.success(token);
    }

    @PostMapping("/mobileLogin")
    @Operation(summary ="移动端用户登录")
    public ApiResult<String> mobileLogin(@Valid @RequestBody LoginReq loginReq) {
        String token = loginService.mobileLogin(loginReq);
        return ApiResult.success(token);
    }

    @PostMapping("/logout")
    @Operation(summary ="用户登录")
    public ApiResult<Boolean> logout() {
        loginService.logout();
        return ApiResult.success(Boolean.TRUE);
    }

    @PostMapping("/register")
    @Operation(summary ="用户注册")
    public ApiResult<String> register(@Valid @RequestBody RegisterReq registerReq) {
        loginService.register(User.builder()
                .account(registerReq.getAccount())
                .password(registerReq.getPassword())
                .name(registerReq.getName()).build());
        return ApiResult.success(registerReq.getAccount());
    }

    @PostMapping("/check")
    @Operation(summary ="用户验证是否过期")
    public ApiResult<Boolean> check() {
        // 延长token时间
        tokenService.refreshToken();
        return ApiResult.success(Boolean.TRUE);
    }

}

