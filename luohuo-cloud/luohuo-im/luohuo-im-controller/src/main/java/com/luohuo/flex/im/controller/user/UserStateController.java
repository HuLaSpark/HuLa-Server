package com.luohuo.flex.im.controller.user;

import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.entity.UserState;
import com.luohuo.flex.im.core.user.service.UserStateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户在线状态管理
 */
@RestController
@RequestMapping("/user/state/")
public class UserStateController {

	@Resource
	private UserStateService userStateService;

	@PostMapping("changeState/{id}")
	@Operation(summary = "用户状态改变")
	public R<Boolean> changeState(@PathVariable("id") Long id){
		return R.success(userStateService.changeState(ContextUtil.getUid(), id));
	}

	@GetMapping("list")
	@Operation(summary = "获取所有用户状态")
	public R<List<UserState>> list(){
		return R.success(userStateService.list());
	}
}
