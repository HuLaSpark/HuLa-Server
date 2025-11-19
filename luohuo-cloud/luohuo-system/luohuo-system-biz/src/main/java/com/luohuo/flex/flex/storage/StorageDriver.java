package com.luohuo.flex.flex.storage;

import com.alibaba.fastjson.JSONObject;
import com.luohuo.flex.flex.storage.engine.QiNiuStorage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
		this.engine = sysConfigService.get("storageDefault");
		config.put("storagePrefix", sysConfigService.get("qnStorageName"));
		config.put("qnUploadUrl", sysConfigService.get("qnUploadUrl"));
		config.put("qnAccessKey", sysConfigService.get("qnAccessKey"));
		config.put("qnSecretKey", sysConfigService.get("qnSecretKey"));
		config.put("qnStorageName", sysConfigService.get("qnStorageName"));
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

	/**
	 * 上传字节数组到存储服务
	 *
	 * @param data 文件字节数组
	 * @param fileExtension 文件扩展名（如 "mp3"）
	 * @return 文件访问URL
	 */
	public String uploadBytes(byte[] data, String fileExtension) {
		switch (this.engine) {
			case "qiniu" -> {
				return new QiNiuStorage(this.getConfig()).uploadBytes(data, fileExtension);
			}
			default -> throw new IllegalArgumentException("不支持的存储引擎: " + this.engine);
		}
	}
}
