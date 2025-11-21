package com.luohuo.basic.scan;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.luohuo.basic.scan.properties.ScanProperties;
import com.luohuo.basic.scan.utils.RequestMappingScanUtils;

/**
 * 启动配置
 *
 * @author 乾乾
 * @date 2021年09月19日15:13:33
 */
@AllArgsConstructor
@ComponentScan(
        basePackages = {
                "com.luohuo.basic.scan.controller"
        }
)
@Configuration
@EnableConfigurationProperties(ScanProperties.class)
public class ScanAutoConfigure {

    @Bean
    @ConditionalOnClass
    @ConditionalOnProperty(prefix = ScanProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public RequestMappingScanUtils getRequestMappingScanUtils(ScanProperties properties, ApplicationContext applicationContext) {
        RequestMappingScanUtils scanUtils = new RequestMappingScanUtils();
        scanUtils.scan(properties.getBasePackage(), applicationContext);
        return scanUtils;
    }
}
