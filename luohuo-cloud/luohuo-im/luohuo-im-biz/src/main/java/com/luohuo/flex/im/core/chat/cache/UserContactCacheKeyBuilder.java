package com.luohuo.flex.im.core.chat.cache;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 用户所有会话信息的缓存
 * @author 乾乾
 * @date 2025-08-27 10:45 上午
 */
public class UserContactCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheHashKey build(Long id) {
        return new UserContactCacheKeyBuilder().hashFieldKey("concat", id);
    }

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public String getTable() {
        return CacheKeyTable.Chat.USER_CONTACT;
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
        return Duration.ofDays(1L);
    }
}
