package com.luohuo.basic.mq.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.luohuo.basic.cache.RedisAutoConfigure;
import com.luohuo.basic.mq.redis.core.RedisMQTemplate;
import com.luohuo.basic.mq.redis.core.interceptor.RedisMessageInterceptor;

import java.util.List;

/**
 * Redis 消息队列 Producer 配置类
 *
 * @author 乾乾
 */
@Slf4j
@AutoConfiguration(after = RedisAutoConfigure.class)
public class EarthRedisMQProducerAutoConfiguration {

    @Bean
	@ConditionalOnMissingBean(StringRedisTemplate.class)
    public RedisMQTemplate redisMQTemplate(StringRedisTemplate redisTemplate,
										   List<RedisMessageInterceptor> interceptors) {
        RedisMQTemplate redisMQTemplate = new RedisMQTemplate(redisTemplate);
        // 添加拦截器
        interceptors.forEach(redisMQTemplate::addInterceptor);
        return redisMQTemplate;
    }

}
