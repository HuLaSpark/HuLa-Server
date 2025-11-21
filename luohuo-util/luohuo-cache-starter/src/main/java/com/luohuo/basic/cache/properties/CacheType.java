package com.luohuo.basic.cache.properties;

/**
 * 缓存类型
 *
 * @author 乾乾
 * @date 2020/9/22 3:34 下午
 */
public enum CacheType {
    /**
     * 内存
     */
    CAFFEINE,
    /**
     * redis
     */
    REDIS,
    ;

    public boolean eq(CacheType cacheType) {
        return cacheType != null && this.name().equals(cacheType.name());
    }
}
