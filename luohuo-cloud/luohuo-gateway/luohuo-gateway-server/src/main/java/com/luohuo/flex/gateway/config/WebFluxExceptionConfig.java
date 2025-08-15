package com.luohuo.flex.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;

/**
 * WebFlux 全局异常处理器
 */
@Configuration
@Slf4j
public class WebFluxExceptionConfig {

    @Primary
    @Bean
    @Order(-1)
    public WebFluxGlobalExceptionHandler webFluxExceptionHandler(ServerCodecConfigurer serverCodecConfigurer) {
        WebFluxGlobalExceptionHandler exceptionHandler = new WebFluxGlobalExceptionHandler() {
            // 实现抽象方法或直接使用
        };
        
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        return exceptionHandler;
    }
}