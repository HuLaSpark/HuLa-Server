package com.hula.core.user.controller;


import com.hula.common.domain.vo.dto.RequestInfo;
import com.hula.common.domain.vo.resp.ApiResult;
import com.hula.common.utils.RequestHolder;
import com.hula.core.user.domain.entity.vo.resp.UserInfoResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表 前端控制器
 * @author nyh
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        RequestInfo requestInfo = RequestHolder.get();
        System.out.println(requestInfo.getUid());
        return null;
    }
}

