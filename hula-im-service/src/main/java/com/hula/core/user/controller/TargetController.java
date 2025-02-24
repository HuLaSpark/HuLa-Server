package com.hula.core.user.controller;

import com.hula.core.chat.domain.vo.request.room.TargetParam;
import com.hula.core.chat.domain.vo.request.room.UserTargetRelParam;
import com.hula.core.user.service.TargetService;
import com.hula.core.user.service.UserTargetRelService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/target")
@RequiredArgsConstructor
public class TargetController {

	private TargetService targetService;

	private UserTargetRelService userTargetRelService;

	@PostMapping("/add")
	public ApiResult save(@RequestBody TargetParam param){
		return ApiResult.success(targetService.save(RequestHolder.get().getUid(), param));
	}

	@PostMapping("/edit")
	public ApiResult edit(@RequestBody TargetParam param){
		return ApiResult.success(targetService.edit(param));
	}

	@GetMapping("/detail")
	public ApiResult detail(@RequestParam("id")Long id){
		return ApiResult.success(targetService.detail(id));
	}

	@PostMapping("/del")
	public ApiResult del(@RequestBody List<Long> ids){
		return ApiResult.success(targetService.removeByIds(ids));
	}

	@GetMapping("/rel/detail")
	@Operation(summary = "查询好友的标签列表")
	public ApiResult relDetail(@RequestParam("friendId") Long friendId) {
		return ApiResult.success(userTargetRelService.detail(RequestHolder.get().getUid(), friendId));
	}

	@Operation(summary = "标签绑定修改")
	@PostMapping("/rel/edit")
	public ApiResult editTarget(@RequestBody UserTargetRelParam param){
		return ApiResult.success(userTargetRelService.editTarget(RequestHolder.get().getUid(), param));
	}
}
