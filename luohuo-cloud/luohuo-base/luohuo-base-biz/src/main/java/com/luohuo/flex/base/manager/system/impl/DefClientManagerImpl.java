package com.luohuo.flex.base.manager.system.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.impl.SuperCacheManagerImpl;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.system.DefClient;
import com.luohuo.flex.base.manager.system.DefClientManager;
import com.luohuo.flex.base.mapper.system.DefClientMapper;
import com.luohuo.flex.common.cache.tenant.system.DefClientCacheKeyBuilder;
import com.luohuo.flex.common.cache.tenant.system.DefClientSecretCacheKeyBuilder;

/**
 * <p>
 * 通用业务实现类
 * 客户端
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-13
 * @create [2021-10-13] [zuihou] [代码生成器生成]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefClientManagerImpl extends SuperCacheManagerImpl<DefClientMapper, DefClient> implements DefClientManager {
    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new DefClientCacheKeyBuilder();
    }

    @Override
    public DefClient getClient(String clientId, String clientSecret) {
        CacheKey key = DefClientSecretCacheKeyBuilder.builder(clientId, clientSecret);
        CacheResult<Long> result = cacheOps.get(key, k -> {
            DefClient one = getOne(Wraps.<DefClient>lbQ().eq(DefClient::getClientId, clientId).eq(DefClient::getClientSecret, clientSecret));
            return one == null ? null : one.getId();
        });
        Long id = result.asLong();
        ArgumentAssert.notNull(id, "客户端[{}]不存在", clientId);
        return getByIdCache(id);
    }
}
