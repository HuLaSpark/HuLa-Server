package com.hula.common.storage.engine;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.util.Auth;
import java.util.Map;

/**
 * 七牛云存储
 */
public class QiNiuStorage {

    /**
     * 存储配置
     */
    private final Map<String, String> config;

    /**
     * 构造方法
     */
    public QiNiuStorage(Map<String, String> config) {
        this.config = config;
    }

    /**
     * 鉴权令牌
     *
     * @author 乾乾
     * @return JSONObject
     */
    public JSONObject upToken() {
		String domain = this.config.getOrDefault("qnUploadUrl", "");
        String accessKey = this.config.getOrDefault("qnAccessKey", "");
        String secretKey = this.config.getOrDefault("qnSecretKey", "");
        String bucket    = this.config.getOrDefault("qnStorageName", "");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("domain", domain);
		jsonObject.put("token", Auth.create(accessKey, secretKey).uploadToken(bucket));

		jsonObject.put("storagePrefix", config.get("storagePrefix"));
		return jsonObject;
    }
}
