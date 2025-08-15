package com.luohuo.flex.satoken.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.luohuo.basic.constant.Constants;
import com.luohuo.flex.common.properties.IgnoreProperties;
import com.luohuo.flex.common.properties.SystemProperties;

/**
 * 注册 Sa-Token 框架所需要的 Bean
 * @author tangyh
 * @since 2024/9/18 14:38
 */
@Slf4j
public class MySaTokenContextRegister {

    @Configuration
    @ConditionalOnProperty(prefix = Constants.PROJECT_PREFIX + ".webmvc", name = "header", havingValue = "true", matchIfMissing = true)
    public static class InnerConfig {
        public InnerConfig() {
            log.info("加载：{}", InnerConfig.class.getName());
        }

        @Bean
        @ConditionalOnClass
        public GlobalMvcConfigurer getGlobalMvcConfigurer(SystemProperties systemProperties, IgnoreProperties ignoreProperties) {
            return new GlobalMvcConfigurer(systemProperties);
        }

    }

}
