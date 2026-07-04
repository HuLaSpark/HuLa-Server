package com.luohuo.basic.cache.utils;

/**
 * Redis 对象序列化器。
 *
 * @author 乾乾
 * @date 2019-08-06 10:42
 */
public class RedisObjectSerializer extends FastJsonJsonRedisSerializer<Object> {
	public RedisObjectSerializer() {
		super(Object.class);
	}
}
