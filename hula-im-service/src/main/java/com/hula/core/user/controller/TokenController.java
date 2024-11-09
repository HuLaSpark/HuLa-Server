package com.hula.core.user.controller;


import com.hula.core.user.domain.dto.ItemInfoDTO;
import com.hula.core.user.domain.dto.SummeryInfoDTO;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.RoleEnum;
import com.hula.core.user.domain.vo.req.user.*;
import com.hula.core.user.domain.vo.resp.user.BadgeResp;
import com.hula.core.user.domain.vo.resp.user.UserInfoResp;
import com.hula.core.user.service.LoginService;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.UserService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.AssertUtil;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * token
 * @author ZOL
 */
@RestController
@RequestMapping("/token")
@Tag(name = "用户管理")
public class TokenController {

    @Resource
    private UserService userService;

    @Resource
    private LoginService loginService;

    @GetMapping("/login")
    @Operation(summary ="用户详情")
    public ApiResult<String> login(@Valid @RequestBody LoginReq loginReq) {
        User user = userService.login(loginReq);
        String token = loginService.login(user.getId());
        return ApiResult.success(token);
    }


}

