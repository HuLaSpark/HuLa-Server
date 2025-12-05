package com.luohuo.flex;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luohuo.basic.base.R;
import com.luohuo.flex.entity.Config;
import com.luohuo.flex.entity.Init;
import com.luohuo.flex.mapper.ConfigMapper;
import com.luohuo.flex.service.SysConfigService;
import com.luohuo.flex.flex.storage.StorageDriver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	private final ConfigMapper configMapper;

	@GetMapping("/config/init")
	@Operation(summary = "获取系统全局配置")
	public R<Init> init() {
		return R.success(sysConfigService.getSystemInit());
	}

    @Operation(summary = "获取统一直传凭证（根据引擎返回 七牛token 或 MinIO预签名）")
    @GetMapping("/ossToken")
    public R<JSONObject> token(@RequestParam(required = false, defaultValue = "chat") String scene, @RequestParam(required = false) String fileName) {
        return R.success(storageDriver.getToken(scene, fileName));
    }

	@GetMapping("/storage/provider")
	@Operation(summary = "获取默认存储提供者")
	public R<JSONObject> storageProvider() {
		var provider = sysConfigService.get("storageDefault");
		var json = new JSONObject();
		json.put("provider", provider);
		return R.success(json);
	}

	@GetMapping("/config/list")
	@Operation(summary = "获取配置列表")
	public R<List<Config>> configList(
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String configName,
			@RequestParam(required = false) String configKey) {
		var wrapper = Wrappers.<Config>lambdaQuery()
				.eq(type != null, Config::getType, type)
				.like(configName != null, Config::getConfigName, configName)
				.like(configKey != null, Config::getConfigKey, configKey)
				.orderByDesc(Config::getId);
		return R.success(configMapper.selectList(wrapper));
	}

	@PutMapping("/config/update")
	@Operation(summary = "更新配置")
	public R<Boolean> updateConfig(@RequestBody Config config) {
		int rows = configMapper.updateById(config);
		if (rows > 0) {
			// 清空配置缓存
			sysConfigService.clearConfigCache();
			// 重新加载配置到缓存
			sysConfigService.loadingConfigCache();
		}
		return R.success(rows > 0);
	}

	@PutMapping("/config/batchUpdate")
	@Operation(summary = "批量更新配置")
	public R<Boolean> batchUpdateConfig(@RequestBody List<Config> configs) {
		for (Config config : configs) {
			configMapper.updateById(config);
		}
		// 清空配置缓存
		sysConfigService.clearConfigCache();
		// 重新加载配置到缓存
		sysConfigService.loadingConfigCache();
		return R.success(true);
	}
}
