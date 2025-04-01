package com.hula.ai.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 系统配置参数
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Component
public class SystemConfig {

    /**
     * 本地上传文件地址
     */
    public static String uploadPath;

    /**
     * 当前项目域名
     */
    public static String baseUrl;


    @Value("${system.upload-path:default}")
    public void setUploadPath(String uploadPath) {
        SystemConfig.uploadPath = uploadPath;
        File file = new File(uploadPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Value("${url.base:default}")
    public void setBaseUrl(String baseUrl) {
        SystemConfig.baseUrl = baseUrl;
    }

}
