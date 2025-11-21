package com.luohuo.flex.common.cache.base.system;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 角色 KEY
 * <p>
 *
 * @author 乾乾
 * @date 2020/9/20 6:45 下午
 */
public class RoleCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheKey build(Long roleId) {
        return new RoleCacheKeyBuilder().key(roleId);
    }

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public String getTable() {
        return CacheKeyTable.Base.ROLE;
    }

    @Override
    public String getPrefix() {
        return CacheKeyModular.PREFIX;
    }

    @Override
    public String getModular() {
        return CacheKeyModular.BASE;
    }

    @Override
    public String getField() {
        return SuperEntity.ID_FIELD;
    }

    @Override
    public Duration getExpire() {
        return Duration.ofHours(24);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.obj;
    }
}
