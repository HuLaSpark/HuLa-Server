package com.luohuo.basic.cache.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.luohuo.basic.utils.StrPool;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * Redis使用FastJson序列化
 *
 * @author 乾乾
 */
public class FastJsonJsonRedisSerializer<T> implements RedisSerializer<T> {
	public static final Charset DEFAULT_CHARSET = Charset.forName(StrPool.UTF8);

	private Class<T> clazz;

	public FastJsonJsonRedisSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return new byte[0];
		}
//		return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(DEFAULT_CHARSET);
		return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		String str = new String(bytes, DEFAULT_CHARSET);
//		return JSON.parseObject(str, clazz, JSONReader.Feature.SupportAutoType);
		return JSON.parseObject(str, clazz, ParserConfig.getGlobalInstance(), Feature.SupportAutoType);
	}
}

