package com.luohuo.basic.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.luohuo.basic.constant.Constants;

import java.time.Duration;
import java.util.Map;

/**
 * 缓存配置
 *
 * @author 乾乾
 * @date 2019/08/06
 */
@Data
@ConfigurationProperties(prefix = CustomCacheProperties.PREFIX)
public class CustomCacheProperties {
    public static final String PREFIX = Constants.PROJECT_PREFIX + ".cache";

    /**
     * {@link #redisScanBatchSize} 默认值
     */
    private static final Integer REDIS_SCAN_BATCH_SIZE_DEFAULT = 30;

    /**
     * redis scan 一次返回数量
     */
    private Integer redisScanBatchSize = REDIS_SCAN_BATCH_SIZE_DEFAULT;
    /**
     * 目前只支持 REDIS 和 CAFFEINE ！
     * CAFFEINE 只用于项目的开发环境或者演示环境使用，  生产环境请用redis！！！
     */
    private CacheType type = CacheType.REDIS;
    /**
     * 序列化类型
     */
    private SerializerType serializerType = SerializerType.ProtoStuff;
    /**
     * 是否缓存 null 值
     */
    private Boolean cacheNullVal = true;

    /**
     * 通过 @Cacheable 注解标注的方法的缓存策略
     */
    private Cache def = new Cache();
    /**
     * 针对某几个具体的key特殊配置
     * <p>
     * 改属性只对 redis 有效！！！
     * configs的key需要配置成@Cacheable注解的value
     */
    private Map<String, Cache> configs;

    @Data
    public static class Cache {

        /**
         * key 的过期时间
         * 默认1天过期
         * eg:
         * timeToLive: 1d
         */
        private Duration timeToLive = Duration.ofDays(1);

        /**
         * 是否允许缓存null值
         */
        private boolean cacheNullValues = true;

        /**
         * key 的前缀
         * 最后的key格式： keyPrefix + @Cacheable.value + @Cacheable.key
         * <p>
         * 使用场景： 开发/测试环境 或者 演示/生产 环境，为了节省资源，往往会共用一个redis，即可以根据key前缀来区分(当然，也能用切换 database 来实现)
         */
        private String keyPrefix;

        /**
         * 写入redis时，是否使用key前缀
         */
        private boolean useKeyPrefix = true;

        /**
         * Caffeine 的最大缓存个数
         */
        private int maxSize = 1000;

    }

}
