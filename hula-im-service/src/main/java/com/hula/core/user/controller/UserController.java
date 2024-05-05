package com.hula.core.user.controller;


import com.hula.common.domain.vo.resp.ApiResult;
import com.hula.common.utils.AssertUtil;
import com.hula.common.utils.RequestHolder;
import com.hula.core.user.domain.enums.RoleEnum;
import com.hula.core.user.domain.vo.req.BlackReq;
import com.hula.core.user.domain.vo.req.ModifyNameReq;
import com.hula.core.user.domain.vo.req.WearingBadgeReq;
import com.hula.core.user.domain.vo.resp.BadgeResp;
import com.hula.core.user.domain.vo.resp.UserInfoResp;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户表 前端控制器
 * @author nyh
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @Operation(summary = "修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req.getName());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @Operation(summary = "可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @Operation(summary = "佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req.getBadgeId());
        return ApiResult.success();
    }

    @PutMapping("/black")
    @Operation(summary = "拉黑用户")
    public ApiResult<Void> black(@javax.validation.Valid @RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "没有权限");
        userService.black(req);
        return ApiResult.success();
    }

}

