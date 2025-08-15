package com.luohuo.flex.oauth.service.storage;

import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.luohuo.flex.oauth.service.storage.engine.QiNiuStorage;
import com.luohuo.flex.service.SysConfigService;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageDriver {

	private final SysConfigService sysConfigService;

    /**
     * 当前存储引擎
     */
    private String engine;

    /**
     * 存储引擎配置
     */
    private Map<String, String> config = new HashMap<>();

	@PostConstruct
	public void init() {
//		this.engine = sysConfigService.get("storageDefault");
//		config.put("storagePrefix", sysConfigService.get("qnStorageName"));
//		config.put("qnUploadUrl", sysConfigService.get("qnUploadUrl"));
//		config.put("qnAccessKey", sysConfigService.get("qnAccessKey"));
//		config.put("qnSecretKey", sysConfigService.get("qnSecretKey"));
//		config.put("qnStorageName", sysConfigService.get("qnStorageName"));
	}

	public Map<String, String> getConfig() {
		return config;
	}


	public JSONObject getToken() {
		switch (this.engine) {
			case "qiniu" -> {
				return new QiNiuStorage(this.getConfig()).upToken();
			}
		}
		return null;
	}
}
