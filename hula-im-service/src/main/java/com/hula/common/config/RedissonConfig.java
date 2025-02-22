package com.hula.common.config;

import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nyh
 */
@Configuration
public class RedissonConfig {
    @Resource
    private RedisProperties redisProperties;

    @Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
				.setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
				// redis 6.x 需要设置用户名
				.setUsername("default")
				// 记得打开密码设置
				.setPassword(redisProperties.getPassword())
				// .setPassword(null)
				.setDatabase(redisProperties.getDatabase());
		return Redisson.create(config);
	}
}
