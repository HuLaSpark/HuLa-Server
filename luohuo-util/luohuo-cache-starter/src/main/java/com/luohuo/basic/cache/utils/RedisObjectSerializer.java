package com.luohuo.basic.cache.utils;

/**
 * 此时定义的序列化操作表示可以序列化所有类的对象，当然，这个对象所在的类一定要实现序列化接口
 *
 * @author 乾乾
 * @date 2019-08-06 10:42
 */
//public class RedisObjectSerializer extends Jackson2JsonRedisSerializer<Object> {
//    public RedisObjectSerializer() {
//        super(buildObjectMapper(), Object.class);
//    }
//
//    private static ObjectMapper buildObjectMapper() {
//        ObjectMapper objectMapper = JsonUtil.newInstance();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.WRAPPER_ARRAY);
//        return objectMapper;
//    }
//}
public class RedisObjectSerializer extends FastJsonJsonRedisSerializer<Object> {
	public RedisObjectSerializer() {
		super(Object.class);
	}
}
