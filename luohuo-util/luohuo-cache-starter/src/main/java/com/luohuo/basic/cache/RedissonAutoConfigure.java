package com.luohuo.basic.cache;

import com.luohuo.basic.cache.properties.CustomCacheProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = CustomCacheProperties.PREFIX, name = "type", havingValue = "REDIS", matchIfMissing = true)
@EnableConfigurationProperties(DataRedisProperties.class)
public class RedissonAutoConfigure {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(DataRedisProperties redisProperties) {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setDatabase(redisProperties.getDatabase());
        if (StringUtils.hasText(redisProperties.getUsername())) {
            singleServerConfig.setUsername(redisProperties.getUsername());
        }
        if (StringUtils.hasText(redisProperties.getPassword())) {
            singleServerConfig.setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }
}