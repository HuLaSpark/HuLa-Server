package com.luohuo.basic.tenant.core.redis;

import cn.hutool.core.collection.CollUtil;
import com.luohuo.basic.cache.redis.TimeoutRedisCacheManager;
import com.luohuo.basic.context.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Set;

/**
 * 多租户的 {@link RedisCacheManager} 实现类
 *
 * 操作指定 name 的 {@link Cache} 时，自动拼接租户后缀，格式为 name + ":" + tenantId + 后缀
 */
@Slf4j
public class TenantRedisCacheManager extends TimeoutRedisCacheManager {

    private final Set<String> ignoreCaches;

    public TenantRedisCacheManager(RedisCacheWriter cacheWriter,
                                   RedisCacheConfiguration defaultCacheConfiguration,
                                   Set<String> ignoreCaches) {
        super(cacheWriter, defaultCacheConfiguration);
        this.ignoreCaches = ignoreCaches;
    }

    @Override
    public Cache getCache(String name) {
        // 如果开启多租户，则 name 拼接租户后缀
        if (!ContextUtil.isIgnore()
                && ContextUtil.getTenantId() != null
                && !CollUtil.contains(ignoreCaches, name)) {
            name = name + ":" + ContextUtil.getTenantId();
        }

        // 继续基于父方法
        return super.getCache(name);
    }

}
