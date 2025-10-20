package com.luohuo.flex;

import com.alibaba.fastjson.JSONObject;
import com.luohuo.basic.base.R;
import com.luohuo.flex.entity.Init;
import com.luohuo.flex.service.SysConfigService;
import com.luohuo.flex.flex.storage.StorageDriver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * system模块公开 服务
 *
 * @author 乾乾
 * @date 2025年07月11日00:50:36
 */
@Slf4j
@RestController
@RequestMapping("/anyTenant")
@AllArgsConstructor
@Tag(name = "系统服务")
public class IndexController {

	private final SysConfigService sysConfigService;
	private final StorageDriver storageDriver;

	@GetMapping("/config/init")
	@Operation(summary = "获取系统全局配置")
	public R<Init> init() {
		return R.success(sysConfigService.getSystemInit());
	}

	@Operation(summary = "获取七牛云上传token")
	@GetMapping("/ossToken")
	public R<JSONObject> token() {
		return R.success(storageDriver.getToken());
	}
}
