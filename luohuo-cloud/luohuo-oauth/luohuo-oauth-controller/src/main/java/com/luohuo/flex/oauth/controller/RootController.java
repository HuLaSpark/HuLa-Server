package com.luohuo.flex.oauth.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.annotation.user.LoginUser;
import com.luohuo.basic.tenant.core.aop.TenantIgnore;
import com.luohuo.flex.base.vo.update.tenant.DefUserPasswordUpdateVO;
import com.luohuo.flex.model.entity.system.SysUser;
import com.luohuo.flex.oauth.vo.param.RefreshTokenVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.R;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.oauth.enumeration.GrantType;
import com.luohuo.flex.oauth.granter.RefreshTokenGranter;
import com.luohuo.flex.oauth.granter.TokenGranterBuilder;
import com.luohuo.flex.oauth.service.UserInfoService;
import com.luohuo.flex.oauth.service.storage.StorageDriver;
import com.luohuo.flex.oauth.vo.param.LoginParamVO;
import com.luohuo.flex.oauth.vo.param.RegisterByEmailVO;
import com.luohuo.flex.oauth.vo.param.RegisterByMobileVO;
import com.luohuo.flex.oauth.vo.result.LoginResultVO;

/**
 * 登录页 Controller
 *
 * @author zuihou
 * @date 2020年03月31日10:10:36
 */
@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Tag(name = "登录-退出-注册")
public class RootController {

    private final TokenGranterBuilder tokenGranterBuilder;
    private final RefreshTokenGranter refreshTokenGranter;
    private final DefUserService defUserService;
    private final UserInfoService userInfoService;
	private final StorageDriver storageDriver;

    /**
     * 登录接口
     * grantType 表示登录类型 可选值为：CAPTCHA,REFRESH_TOKEN,PASSWORD,MOBILE
     * @author 乾乾
     * @date 2025/06/06 9:33 PM
     */
	@PostMapping(value = "/anyTenant/login")
    @Operation(summary = "登录接口", description = "登录或者清空缓存时调用")
    @TenantIgnore
    public R<LoginResultVO> login(@Validated @RequestBody LoginParamVO login) throws BizException {
        return tokenGranterBuilder.getGranter(login.getGrantType()).login(login);
    }

    @Operation(summary = "刷新token", description = "token过期时，刷新token使用")
    @PostMapping("/anyTenant/refresh")
    public R<LoginResultVO> refresh(@RequestBody RefreshTokenVO refreshToken) throws BizException {
        return R.success(refreshTokenGranter.refresh(refreshToken.getRefreshToken()));
    }

    @Operation(summary = "修改密码", description = "修改密码")
    @PutMapping("/anyTenant/password")
    @WebLog("'修改密码:' + #data.id")
    public R<Boolean> updatePassword(@RequestBody @Validated DefUserPasswordUpdateVO data) {
        return R.success(defUserService.updatePassword(data));
    }

    @Operation(summary = "切换部门")
    @PutMapping("/anyone/switchTenantAndOrg")
    public R<LoginResultVO> switchOrg(@RequestParam(required = false) Long orgId) {
        return R.success(tokenGranterBuilder.getGranter(GrantType.PASSWORD).switchOrg(orgId));
    }

    @Operation(summary = "退出", description = "退出")
    @PostMapping("/anyUser/logout")
    public R<Boolean> logout() {
        return tokenGranterBuilder.getGranter().logout();
    }

    @Operation(summary = "验证token是否正确", description = "验证token")
    @GetMapping("/anyTenant/verify")
    public R<SaSession> verify(@RequestParam("token") String token) throws BizException {
        return R.success(StpUtil.getTokenSessionByToken(token));
    }

    @Operation(summary = "根据手机号注册", description = "根据手机号注册")
    @PostMapping("/anyTenant/registerByMobile")
    public R<String> register(@Validated @RequestBody RegisterByMobileVO register) throws BizException {
        return R.success(userInfoService.registerByMobile(register));
    }

    @Operation(summary = "根据邮箱注册", description = "根据邮箱注册")
    @PostMapping("/anyTenant/registerByEmail")
    public R<String> register(@Parameter(hidden = true) @LoginUser(isEmployee = true) SysUser sysUser, @Validated @RequestBody RegisterByEmailVO register) throws BizException {
        return R.success(userInfoService.registerByEmail(sysUser, register));
    }

    @Operation(summary = "检测手机号是否存在")
    @GetMapping("/anyTenant/checkMobile")
    public R<Boolean> checkMobile(@RequestParam String mobile) {
        return R.success(defUserService.checkMobile(mobile, null));
    }

	@Operation(summary = "获取七牛云上传token")
	@GetMapping("/anyTenant/ossToken")
	public R<JSONObject> token() {
		return R.success(storageDriver.getToken());
	}
}
