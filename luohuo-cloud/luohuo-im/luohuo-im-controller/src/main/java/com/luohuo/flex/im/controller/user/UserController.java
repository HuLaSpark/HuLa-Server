package com.luohuo.flex.im.controller.user;

import com.luohuo.basic.tenant.core.aop.TenantIgnore;
import com.luohuo.flex.im.api.vo.UserRegisterVo;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.model.entity.base.RefreshIpInfo;
import com.luohuo.flex.model.entity.base.UserReq;
import com.luohuo.flex.model.vo.query.BindEmailReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.dto.ItemInfoDTO;
import com.luohuo.flex.im.domain.vo.req.user.BlackAddReq;
import com.luohuo.flex.im.domain.vo.req.user.BlackEditReq;
import com.luohuo.flex.im.domain.vo.req.user.BlackPageReq;
import com.luohuo.flex.im.domain.vo.req.user.BlackRemoveReq;
import com.luohuo.flex.im.domain.vo.req.user.UserSearchReq;
import com.luohuo.flex.im.domain.vo.req.user.ItemInfoReq;
import com.luohuo.flex.im.domain.vo.req.user.ModifyAvatarReq;
import com.luohuo.flex.im.domain.vo.req.user.ModifyNameReq;
import com.luohuo.flex.im.domain.vo.req.user.WearingBadgeReq;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.user.BadgeResp;
import com.luohuo.flex.im.domain.vo.resp.user.BlackPageResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserSearchResp;
import com.luohuo.flex.im.core.user.service.UserService;

import java.util.List;

/**
 * 用户表 前端控制器
 * @author 乾乾
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    @Resource
    private UserService userService;

	@PostMapping("refreshIpInfo")
	@Operation(summary ="刷新IP信息、物理地址")
	public R<Boolean> refreshIpInfo(@RequestBody RefreshIpInfo refreshIpInfo) {
		return R.success(userService.refreshIpInfo(refreshIpInfo.getUid(), refreshIpInfo.getIpInfo()));
	}

	@PostMapping("getUserByIds")
	@Operation(summary ="查询用户id")
	public R<List<SummeryInfoDTO>> getUserByIds(@RequestBody UserReq userReq) {
		return R.success(userService.getUserInfo(userReq.getUidList()));
	}

	@GetMapping("/checkEmail")
	@Operation(summary ="绑定邮箱")
	@TenantIgnore
	public R<Boolean> checkEmail(@RequestParam("email") String email) {
		return R.success(userService.checkEmail(email));
	}

	@GetMapping("/getById/{id}")
	@Operation(summary ="用户详情 [仅远程接口调用]")
	public R<UserInfoResp> getById(@PathVariable("id") Long id) {
		return R.success(userService.getUserInfo(id));
	}

	@GetMapping("/findById")
	@Operation(summary ="根据DefUserId查询im用户的id")
	public R<Long> findById(@RequestParam("id") Long defUserId, @RequestParam("tenantId") Long tenantId) {
		return R.success(userService.getUIdByUserId(defUserId, tenantId));
	}

    @PostMapping("/register")
    @TenantIgnore
    public R<Boolean> register(@Valid @RequestBody UserRegisterVo userRegisterVo){
        return R.success(userService.register(userRegisterVo));
    }

    @GetMapping("/userInfo")
    @Operation(summary ="用户详情")
    public R<UserInfoResp> getUserInfo() {
        return R.success(userService.getUserInfo(ContextUtil.getUid()));
    }

    @PostMapping("/avatar")
    @Operation(summary ="修改头像")
	@Deprecated
//	@FrequencyControl(target = FrequencyControl.Target.UID, time = 30, unit = TimeUnit.DAYS)
    public R<Void> modifyAvatar(@Valid @RequestBody ModifyAvatarReq req) {
        userService.modifyAvatar(ContextUtil.getUid(), req);
        return R.success();
    }

	@PostMapping("/bindEmail")
	@Operation(summary ="绑定邮箱")
	public R<Boolean> bindEmail(@Valid @RequestBody BindEmailReq req) {
		return R.success(userService.bindEmail(ContextUtil.getUid(), req));
	}

    @PostMapping("/badges/batch")
    @Operation(summary ="徽章聚合信息-返回的代表需要刷新的")
    public R<List<ItemInfoDTO>> getItemInfo(@Valid @RequestBody ItemInfoReq req) {
        return R.success(userService.getItemInfo(req));
    }

    @PutMapping("/info")
    @Operation(summary ="修改用户信息")
    public R<Void> modifyInfo(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyInfo(ContextUtil.getUid(), req);
        return R.success();
    }

    @GetMapping("/badges")
    @Operation(summary ="可选徽章预览")
    public R<List<BadgeResp>> badges() {
        return R.success(userService.badges(ContextUtil.getUid()));
    }

    @PutMapping("/badge")
    @Operation(summary ="佩戴徽章")
    public R<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(ContextUtil.getUid(), req);
        return R.success();
    }

    @GetMapping("/black/page")
    @Operation(summary ="黑名单列表")
    public R<PageBaseResp<BlackPageResp>> blackPage(@Valid BlackPageReq request) {
        return R.success(userService.blackPage(request));
    }

    @PutMapping("/black")
    @Operation(summary ="拉黑（支持IP和UID）")
    public R<Void> black(@Valid @RequestBody BlackAddReq req) {
        userService.addBlack(req);
        return R.success();
    }

    @PostMapping("/black/edit")
    @Operation(summary ="编辑黑名单")
    public R<Void> editBlack(@Valid @RequestBody BlackEditReq req) {
        userService.editBlack(req);
        return R.success();
    }

    @PostMapping("/black/remove")
    @Operation(summary ="移除黑名单")
    public R<Void> removeBlack(@Valid @RequestBody BlackRemoveReq req) {
        userService.removeBlack(req);
        return R.success();
    }

    @GetMapping("/search")
    @Operation(summary ="搜索用户（按昵称模糊查询）")
    public R<PageBaseResp<UserSearchResp>> searchUser(@Valid UserSearchReq req) {
        return R.success(userService.searchUser(req));
    }

}
