package com.hula.common.user.controller;


import com.hula.common.user.domain.entity.vo.resp.UserInfoResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public UserInfoResp getUserInfo(@RequestParam Long uid) {
        return null;
    }
}

