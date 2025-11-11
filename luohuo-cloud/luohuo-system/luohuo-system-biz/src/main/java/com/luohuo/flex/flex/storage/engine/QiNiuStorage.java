package com.luohuo.flex.flex.storage.engine;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 七牛云存储
 */
@Slf4j
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

    /**
     * 上传字节数组到七牛云
     *
     * @param data 文件字节数组
     * @param fileExtension 文件扩展名（如 "mp3"）
     * @return 文件访问URL
     */
    public String uploadBytes(byte[] data, String fileExtension) {
        try {
            String accessKey = this.config.getOrDefault("qnAccessKey", "");
            String secretKey = this.config.getOrDefault("qnSecretKey", "");
            String bucket = this.config.getOrDefault("qnStorageName", "");
            String cdnDomain = this.config.getOrDefault("qnStorageCDN", "");

            // 创建认证对象
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            // 配置上传区域（自动识别）
            Configuration cfg = new Configuration(Region.autoRegion());
            UploadManager uploadManager = new UploadManager(cfg);

            // 生成唯一文件名
            String fileName = IdUtil.fastSimpleUUID() + "." + fileExtension;

            // 上传文件
            Response response = uploadManager.put(data, fileName, upToken);

            if (response.isOK()) {
                // 返回CDN访问地址
                String url = cdnDomain;
                if (!url.endsWith("/")) {
                    url += "/";
                }
                url += fileName;
                log.info("[uploadBytes][文件上传成功，fileName: {}, url: {}]", fileName, url);
                return url;
            } else {
                log.error("[uploadBytes][文件上传失败，response: {}]", response.bodyString());
                throw new RuntimeException("七牛云上传失败: " + response.bodyString());
            }
        } catch (Exception e) {
            log.error("[uploadBytes][文件上传异常]", e);
            throw new RuntimeException("七牛云上传异常: " + e.getMessage(), e);
        }
    }
}
