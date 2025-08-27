package com.luohuo.basic.cache.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Redis使用FastJson序列化
 *
 * @author 乾乾
 */
public class FastJsonJsonRedisSerializer<T> implements RedisSerializer<T> {
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	private final Class<T> clazz;
	private static final ParserConfig SAFE_CONFIG = new ParserConfig();

	static {
		// 1. 禁用高危特性
		SAFE_CONFIG.setAutoTypeSupport(false);
		// 2. 白名单控制（按需添加）
		SAFE_CONFIG.addAccept("com.luohuo.flex.im.domain.");
		// 3. 启用ASM加速（非Android环境）
		SAFE_CONFIG.setAsmEnable(true);
		// 4. 开启安全模式（FastJSON 1.2.68+）
		SAFE_CONFIG.setSafeMode(true);
	}

	public FastJsonJsonRedisSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return new byte[0];
		}
		return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
//		return JSON.parseObject(
//				bytes,                    // 字节数组
//				0,                        // 起始偏移量
//				bytes.length,             // 长度
//				DEFAULT_CHARSET,          // 字符集
//				clazz,                    // 目标类型
//				ParserConfig.getGlobalInstance(), // 解析配置
//				null,                     // ParseProcess
//				JSON.DEFAULT_PARSER_FEATURE,      // 默认解析特性
//				Feature.SupportArrayToBean,
//				Feature.DisableCircularReferenceDetect
//		);
		String str = new String(bytes, DEFAULT_CHARSET);
		return JSON.parseObject(str, clazz, ParserConfig.getGlobalInstance(), Feature.SupportAutoType);
	}
}

