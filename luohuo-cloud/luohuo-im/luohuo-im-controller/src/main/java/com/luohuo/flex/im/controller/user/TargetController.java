package com.luohuo.flex.im.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.vo.request.room.TargetParam;
import com.luohuo.flex.im.domain.vo.request.room.TargetVo;
import com.luohuo.flex.im.domain.vo.request.room.UserTargetRelParam;
import com.luohuo.flex.im.domain.entity.Target;
import com.luohuo.flex.im.core.user.service.TargetService;
import com.luohuo.flex.im.core.user.service.UserTargetRelService;
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
@Tag(name = "好友标签")
@RequiredArgsConstructor
public class TargetController {

	private TargetService targetService;

	private UserTargetRelService userTargetRelService;

	@PostMapping("/add")
	@Operation(summary = "给好友创建一个标签")
	public R<Boolean> save(@RequestBody TargetParam param){
		return R.success(targetService.save(ContextUtil.getUid(), param));
	}

	@PostMapping("/edit")
	@Operation(summary = "编辑`我`创建的标签")
	public R<Boolean> edit(@RequestBody TargetParam param){
		return R.success(targetService.edit(param));
	}

	@GetMapping("/detail")
	@Operation(summary = "查看`我`创建的标签")
	public R<Target> detail(@RequestParam("id") Long id){
		return R.success(targetService.detail(id));
	}

	@PostMapping("/del")
	@Operation(summary = "删除好友标签")
	public R<Boolean> del(@RequestBody List<Long> ids){
		return R.success(targetService.removeByIds(ids));
	}

	@GetMapping("/rel/detail")
	@Operation(summary = "查询好友的标签列表")
	public R<List<TargetVo>> relDetail(@RequestParam("friendId") Long friendId) {
		return R.success(userTargetRelService.detail(ContextUtil.getUid(), friendId));
	}

	@Operation(summary = "标签绑定修改")
	@PostMapping("/rel/edit")
	public R<Boolean> editTarget(@RequestBody UserTargetRelParam param){
		return R.success(userTargetRelService.editTarget(ContextUtil.getUid(), param));
	}
}
