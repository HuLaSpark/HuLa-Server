package com.hula.common.storage;

import com.alibaba.fastjson.JSONObject;
import com.hula.common.storage.engine.QiNiuStorage;
import com.hula.core.user.service.ConfigService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageDriver {

	private final ConfigService configService;

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
		this.engine = configService.get("storageDefault");
		config.put("storagePrefix", configService.get("qnStorageName"));
		config.put("qnUploadUrl", configService.get("qnUploadUrl"));
		config.put("qnAccessKey", configService.get("qnAccessKey"));
		config.put("qnSecretKey", configService.get("qnSecretKey"));
		config.put("qnStorageName", configService.get("qnStorageName"));
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
