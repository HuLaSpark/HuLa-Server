package com.luohuo.flex.flex.storage.engine;

import com.alibaba.fastjson.JSONObject;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MinioStorage {

    private final Map<String, String> config;

    public MinioStorage(Map<String, String> config) {
        this.config = config;
    }

    public JSONObject presignPut(String objectKey) {
        String endpoint = this.config.getOrDefault("minioEndpoint", "");
        String accessKey = this.config.getOrDefault("minioAccessKey", "");
        String secretKey = this.config.getOrDefault("minioSecretKey", "");
        String bucket = this.config.getOrDefault("minioBucket", "");
        String urlPrefix = this.config.getOrDefault("minioUrlPrefix", "");

        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            String uploadUrl = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(60 * 60)
                            .build()
            );

            String downloadUrl;
            if (urlPrefix != null && !urlPrefix.isBlank()) {
                downloadUrl = urlPrefix.endsWith("/") ? (urlPrefix + objectKey) : (urlPrefix + "/" + objectKey);
            } else {
                String base = endpoint.endsWith("/") ? endpoint : (endpoint + "/");
                downloadUrl = base + bucket + "/" + objectKey;
            }

            JSONObject json = new JSONObject();
            json.put("uploadUrl", uploadUrl);
            json.put("downloadUrl", downloadUrl);
            json.put("objectKey", objectKey);
            return json;
        } catch (Exception e) {
            log.error("[Minio presignPut] 生成预签名失败, objectKey:{}", objectKey, e);
            throw new RuntimeException("MinIO 预签名失败: " + e.getMessage(), e);
        }
    }
}
