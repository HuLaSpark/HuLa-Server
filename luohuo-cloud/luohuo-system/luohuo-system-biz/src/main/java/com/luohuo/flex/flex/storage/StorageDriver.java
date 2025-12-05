package com.luohuo.flex.flex.storage;

import com.alibaba.fastjson.JSONObject;
import com.luohuo.flex.flex.storage.engine.QiNiuStorage;
import com.luohuo.flex.flex.storage.engine.MinioStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.luohuo.flex.service.SysConfigService;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageDriver {

    private final SysConfigService sysConfigService;

    private String currentEngine() {
        String e = sysConfigService.get("storageDefault");
        return e == null || e.isBlank() ? "qiniu" : e;
    }

    public Map<String, String> getConfig() {
        Map<String, String> cfg = new HashMap<>();
        cfg.put("storagePrefix", sysConfigService.get("qnStorageName"));
        cfg.put("qnUploadUrl", sysConfigService.get("qnUploadUrl"));
        cfg.put("qnAccessKey", sysConfigService.get("qnAccessKey"));
        cfg.put("qnSecretKey", sysConfigService.get("qnSecretKey"));
        cfg.put("qnStorageName", sysConfigService.get("qnStorageName"));
        cfg.put("minioEndpoint", sysConfigService.get("minioEndpoint"));
        cfg.put("minioAccessKey", sysConfigService.get("minioAccessKey"));
        cfg.put("minioSecretKey", sysConfigService.get("minioSecretKey"));
        cfg.put("minioBucket", sysConfigService.get("minioBucket"));
        cfg.put("minioUrlPrefix", sysConfigService.get("minioUrlPrefix"));
        return cfg;
    }

    public JSONObject getToken(String scene, String fileName) {
        String engine = currentEngine();
        Map<String, String> cfg = getConfig();
        switch (engine) {
            case "qiniu" -> {
                return new QiNiuStorage(cfg).upToken();
            }
            case "minio" -> {
                String safeScene = scene == null || scene.isBlank() ? "chat" : scene;
                String safeName = fileName == null || fileName.isBlank() ? "file" : fileName;
                String objectKey = safeScene + "/" + System.currentTimeMillis() + "_" + safeName;
                return new MinioStorage(cfg).presignPut(objectKey);
            }
            default -> throw new IllegalArgumentException("不支持的存储引擎: " + engine);
        }
    }
}
