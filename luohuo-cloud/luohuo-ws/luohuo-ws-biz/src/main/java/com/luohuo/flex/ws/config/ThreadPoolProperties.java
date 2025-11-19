package com.luohuo.flex.ws.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "luohuo.thread")
@Component
@Data
public class ThreadPoolProperties {
    private int coreSize;        // 核心线程数
    private int maxSize;         // 最大线程数
    private int queueCapacity;   // 队列容量
    private int keepAlive;       // 线程空闲超时时间
}