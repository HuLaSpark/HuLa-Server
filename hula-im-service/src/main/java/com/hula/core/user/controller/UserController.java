package com.hula.core.user.controller;


import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.AssertUtil;
import com.hula.utils.RequestHolder;
import com.hula.core.user.domain.dto.ItemInfoDTO;
import com.hula.core.user.domain.dto.SummeryInfoDTO;
import com.hula.core.user.domain.enums.RoleTypeEnum;
import com.hula.core.user.domain.vo.req.user.*;
import com.hula.core.user.domain.vo.resp.user.BadgeResp;
import com.hula.core.user.domain.vo.resp.user.UserInfoResp;
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
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    @GetMapping("/userInfo")
    @Operation(summary ="用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PostMapping("/avatar")
    @Operation(summary ="修改头像")
    public ApiResult<Void> modifyAvatar(@Valid @RequestBody ModifyAvatarReq req) {
        userService.modifyAvatar(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @PostMapping("/summary/userInfo/batch")
    @Operation(summary ="用户聚合信息-返回的代表需要刷新的")
    public ApiResult<List<SummeryInfoDTO>> getSummeryUserInfo(@Valid @RequestBody SummeryInfoReq req) {
        return ApiResult.success(userService.getSummeryUserInfo(req));
    }

    @PostMapping("/badges/batch")
    @Operation(summary ="徽章聚合信息-返回的代表需要刷新的")
    public ApiResult<List<ItemInfoDTO>> getItemInfo(@Valid @RequestBody ItemInfoReq req) {
        return ApiResult.success(userService.getItemInfo(req));
    }

    @PutMapping("/name")
    @Operation(summary ="修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @Operation(summary ="可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @Operation(summary ="佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @PutMapping("/black")
    @Operation(summary ="拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        AssertUtil.isTrue(roleService.hasRole(uid, RoleTypeEnum.ADMIN), "没有权限");
        userService.black(req);
        return ApiResult.success();
    }

}

