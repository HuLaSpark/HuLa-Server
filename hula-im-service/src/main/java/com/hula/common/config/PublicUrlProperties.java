package com.hula.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ZOL
 */
@Data
@ConfigurationProperties(prefix = "public")
public class PublicUrlProperties {

    /**
     * 请求地址白名单数组
     */
    private String[] urls;

}
