package com.luohuo.flex.ws.config;

import com.alibaba.cloud.nacos.NacosServiceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosConfig {
    
    @Bean
    public NacosServiceManager nacosServiceManager() {
        return new NacosServiceManager();
    }
}