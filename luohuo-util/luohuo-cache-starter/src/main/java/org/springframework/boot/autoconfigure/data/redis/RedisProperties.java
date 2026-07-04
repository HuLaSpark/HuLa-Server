package org.springframework.boot.autoconfigure.data.redis;

/**
 * Compatibility bridge for third-party Spring Boot 3 auto-configurations.
 */
@Deprecated(since = "4.0", forRemoval = false)
public class RedisProperties extends org.springframework.boot.data.redis.autoconfigure.DataRedisProperties {
}