package com.luohuo.flex.satoken.config;

import cn.dev33.satoken.context.SaTokenContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.luohuo.basic.constant.Constants;
import com.luohuo.flex.common.properties.IgnoreProperties;
import com.luohuo.flex.common.properties.SystemProperties;
import com.luohuo.flex.satoken.spring.MySaTokenContextForSpringInJakartaServlet;

/**
 * 注册 Sa-Token 框架所需要的 Bean
 * @author tangyh
 * @since 2024/9/18 14:38
 */
@Slf4j
@Configuration
public class MySaTokenContextRegister {

    /**
     * 注册 SaTokenContext Bean，用于 Spring Boot 3 Jakarta Servlet 环境
     * 这个 Bean 必须被注册，否则 SaToken 会报"上下文尚未初始化"的错误
     */
    @Bean
    public SaTokenContext saTokenContext() {
        log.info("注册 SaTokenContext 实现类：MySaTokenContextForSpringInJakartaServlet");
        return new MySaTokenContextForSpringInJakartaServlet();
    }

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
