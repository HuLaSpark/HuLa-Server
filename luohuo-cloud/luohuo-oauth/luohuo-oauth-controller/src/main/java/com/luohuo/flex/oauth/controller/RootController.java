package com.luohuo.flex.oauth.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.annotation.user.LoginUser;
import com.luohuo.basic.tenant.core.aop.TenantIgnore;
import com.luohuo.flex.base.vo.update.tenant.DefUserPasswordUpdateVO;
import com.luohuo.flex.model.entity.system.SysUser;
import com.luohuo.flex.oauth.granter.QrCodeGranter;
import com.luohuo.flex.oauth.vo.param.ConfirmReq;
import com.luohuo.flex.oauth.vo.param.QueryStatusReq;
import com.luohuo.flex.oauth.vo.param.RefreshTokenVO;
import com.luohuo.flex.oauth.vo.param.ScanReq;
import com.luohuo.flex.oauth.vo.result.QrCodeResp;
import com.luohuo.flex.oauth.vo.result.ScanResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

	private final QrCodeGranter qrCodeGranter;
    private final TokenGranterBuilder tokenGranterBuilder;
    private final RefreshTokenGranter refreshTokenGranter;
    private final DefUserService defUserService;
    private final UserInfoService userInfoService;

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
    public R<LoginResultVO> switchOrg(@RequestParam(required = false) Long orgId, @RequestParam String clientId) {
        return R.success(tokenGranterBuilder.getGranter(GrantType.PASSWORD).switchOrg(orgId, clientId));
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

	@Operation(summary = "检测邮箱是否存在")
	@GetMapping("/anyTenant/checkEmail")
	public R<Boolean> checkEmail(@RequestParam("email") String email) {
		return R.success(userInfoService.checkEmail(email));
	}

	@Operation(summary = "检测手机号是否存在")
    @GetMapping("/anyTenant/checkMobile")
    public R<Boolean> checkMobile(@RequestParam("mobile") String mobile) {
        return R.success(defUserService.checkMobile(mobile, null));
    }

	@GetMapping("/anyTenant/qr/generate")
	@Operation(summary = "生成登录二维码")
	public R<QrCodeResp> generateQRCode() {
		return R.success(qrCodeGranter.generateQRCode());
	}

	@GetMapping("/anyTenant/qr/status/query")
	@Operation(summary = "查询二维码状态")
	public R checkStatus(@Valid QueryStatusReq req) {
		return qrCodeGranter.checkStatus(req);
	}

	@PostMapping("/qrcode/scan")
	@Operation(summary = "扫描登录二维码")
	public R<ScanResp> handleScan(@Valid @RequestBody ScanReq req) {
		return R.success(qrCodeGranter.handleScan(req));
	}

	@PostMapping("/qrcode/confirm")
	@Operation(summary = "用户在手机上确认登录")
	public R<Long> confirmLogin(@Valid @RequestBody ConfirmReq req) {
		return R.success(qrCodeGranter.confirmLogin(req));
	}
}
