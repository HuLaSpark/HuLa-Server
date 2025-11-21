package com.luohuo.flex.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.base.entity.user.BaseOrg;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.vo.update.tenant.DefUserAvatarUpdateVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserBaseInfoUpdateVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserEmailUpdateVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserMobileUpdateVO;
import com.luohuo.flex.oauth.biz.OauthUserBiz;
import com.luohuo.flex.oauth.service.CaptchaService;
import com.luohuo.flex.oauth.service.UserInfoService;
import com.luohuo.flex.oauth.vo.result.DefUserInfoResultVO;
import com.luohuo.flex.oauth.vo.result.OrgResultVO;

import java.util.List;

/**
 * 认证Controller
 *
 * @author 乾乾
 * @date 2020年03月31日10:10:36
 */
@Slf4j
@RestController
@RequestMapping("/anyone")
@AllArgsConstructor
@Tag(name = "用户基本信息")
public class UserInfoController {

    private final OauthUserBiz oauthUserBiz;
    private final UserInfoService userInfoService;
    private final DefUserService defUserService;
    private final CaptchaService captchaService;

    @Operation(summary = "获取当前登录的用户信息", description = "获取当前登录的用户信息：登录后，查询用户信息")
    @GetMapping(value = "/getUserInfo")
    public R<DefUserInfoResultVO> getUserInfo() throws BizException {
        return R.success(oauthUserBiz.getUserById(ContextUtil.getUserId()));
    }

    @Operation(summary = "修改头像", description = "修改头像")
    @PutMapping("/avatar")
    @WebLog("'修改头像:' + #data.id")
    public R<Boolean> avatar(@RequestBody @Validated DefUserAvatarUpdateVO data) {
        return R.success(defUserService.updateAvatar(data));
    }

    @Operation(summary = "修改手机", description = "修改手机")
    @PutMapping("/mobile")
    @WebLog("'修改手机:' + #data.mobile")
    public R<Boolean> updateMobile(@RequestBody @Validated DefUserMobileUpdateVO data) {
        R<Boolean> r = captchaService.checkCaptcha(data.getMobile(), data.getTemplateCode(), data.getCode());
        if (!r.getsuccess()) {
            return r;
        }
        return R.success(defUserService.updateMobile(data));
    }

    @Operation(summary = "修改邮箱", description = "修改邮箱")
    @PutMapping("/email")
    @WebLog("'修改邮箱:' + #data.email")
    public R<Boolean> updateEmail(@RequestBody @Validated DefUserEmailUpdateVO data) {
        R<Boolean> r = captchaService.checkCaptcha(data.getEmail(), data.getTemplateCode(), data.getCode());
        if (!r.getsuccess()) {
            return r;
        }
        return R.success(defUserService.updateEmail(data));
    }

    @Operation(summary = "修改基础信息")
    @PutMapping("/baseInfo")
    @WebLog(value = "'修改基础信息:' + #data?.id", request = false)
    public R<Boolean> updateBaseInfo(@RequestBody @Validated DefUserBaseInfoUpdateVO data) {
        return R.success(defUserService.updateBaseInfo(data));
    }

    @Operation(summary = "查询单位和部门")
    @GetMapping("/findCompanyDept")
    @WebLog(value = "根据租户ID查询单位和部门")
    public R<OrgResultVO> findCompanyDept() {
        return R.success(userInfoService.findCompanyAndDept());
    }

    @Operation(summary = "根据单位查询部门")
    @WebLog(value = "根据单位查询部门")
    @GetMapping("/findDeptByCompany")
    public R<List<BaseOrg>> findDeptByCompany(@RequestParam Long companyId, @RequestParam Long employeeId) {
        return R.success(userInfoService.findDeptByCompany(companyId, employeeId));
    }

}
