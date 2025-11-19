package com.luohuo.flex.im.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author nyh
 */
@EnableCaching
@Configuration
public class CacheConfig {

    @Bean("caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 定制化缓存配置
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 写入后5分钟过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 初始容量为100
                .initialCapacity(100)
                // 最大容量为200
                .maximumSize(200));
        return cacheManager;
    }
}
