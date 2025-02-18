package com.hula.core.user.controller;

import com.hula.core.user.service.UserStateService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/state/")
public class UserStateController {

	@Resource
	private UserStateService userStateService;

	@PostMapping("changeState/{id}")
	@Operation(summary = "用户状态改变")
	public ApiResult changeState(@PathVariable("id") Long id){
		return ApiResult.success(userStateService.changeState(RequestHolder.get().getUid(), id));
	}

	@GetMapping("list")
	@Operation(summary = "获取所有用户状态")
	public ApiResult list(){
		return ApiResult.success(userStateService.list());
	}
}
