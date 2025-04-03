package com.hula.core.user.controller;

import com.hula.core.chat.domain.vo.request.room.TargetParam;
import com.hula.core.chat.domain.vo.request.room.TargetVo;
import com.hula.core.chat.domain.vo.request.room.UserTargetRelParam;
import com.hula.core.user.domain.entity.Target;
import com.hula.core.user.service.TargetService;
import com.hula.core.user.service.UserTargetRelService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户标签管理 [给好友分类]
 */
@RestController
@RequestMapping("/target")
@Api(tags = "好友标签")
@RequiredArgsConstructor
public class TargetController {

	private TargetService targetService;

	private UserTargetRelService userTargetRelService;

	@PostMapping("/add")
	@Operation(summary = "给好友创建一个标签")
	public ApiResult<Boolean> save(@RequestBody TargetParam param){
		return ApiResult.success(targetService.save(RequestHolder.get().getUid(), param));
	}

	@PostMapping("/edit")
	@Operation(summary = "编辑`我`创建的标签")
	public ApiResult<Boolean> edit(@RequestBody TargetParam param){
		return ApiResult.success(targetService.edit(param));
	}

	@GetMapping("/detail")
	@Operation(summary = "查看`我`创建的标签")
	public ApiResult<Target> detail(@RequestParam("id") Long id){
		return ApiResult.success(targetService.detail(id));
	}

	@PostMapping("/del")
	@Operation(summary = "删除好友标签")
	public ApiResult<Boolean> del(@RequestBody List<Long> ids){
		return ApiResult.success(targetService.removeByIds(ids));
	}

	@GetMapping("/rel/detail")
	@Operation(summary = "查询好友的标签列表")
	public ApiResult<List<TargetVo>> relDetail(@RequestParam("friendId") Long friendId) {
		return ApiResult.success(userTargetRelService.detail(RequestHolder.get().getUid(), friendId));
	}

	@Operation(summary = "标签绑定修改")
	@PostMapping("/rel/edit")
	public ApiResult<Boolean> editTarget(@RequestBody UserTargetRelParam param){
		return ApiResult.success(userTargetRelService.editTarget(RequestHolder.get().getUid(), param));
	}
}
