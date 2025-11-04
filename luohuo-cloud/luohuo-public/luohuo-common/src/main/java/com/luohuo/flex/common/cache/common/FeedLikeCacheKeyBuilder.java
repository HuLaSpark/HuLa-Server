package com.luohuo.flex.common.cache.common;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 朋友圈点赞缓存  朋友圈ID -> 点赞用户ID列表
 * @author 乾乾
 */
public class FeedLikeCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheHashKey build(Long feedId) {
        return new FeedLikeCacheKeyBuilder().hashKey(feedId);
    }

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public String getTable() {
        return CacheKeyTable.Chat.FEED_LIKE;
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
        return Duration.ofDays(7L);
    }
}
