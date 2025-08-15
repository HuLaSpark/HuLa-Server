package com.luohuo.flex.common.cache.common;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 朋友圈所有信息的缓存  朋友圈ID -> 朋友圈素材集合
 * @author 乾乾
 * @date 2025-03-03 6:45 上午
 */
public class FeedMediaRelCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheHashKey build(Long id) {
        return new FeedMediaRelCacheKeyBuilder().hashKey(id);
    }

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public String getTable() {
        return CacheKeyTable.Chat.FEED_MEDIA;
    }

    @Override
    public String getPrefix() {
        return CacheKeyModular.PREFIX;
    }

    @Override
    public String getModular() {
        return CacheKeyModular.CHAT;
    }

    @Override
    public String getField() {
        return SuperEntity.ID_FIELD;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.obj;
    }

    @Override
    public Duration getExpire() {
        return Duration.ofDays(30L);
    }
}
