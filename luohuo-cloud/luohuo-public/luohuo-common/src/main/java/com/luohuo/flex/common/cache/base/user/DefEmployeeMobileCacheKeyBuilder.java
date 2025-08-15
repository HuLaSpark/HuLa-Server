package com.luohuo.flex.common.cache.base.user;

import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * admin用户 KEY
 * <p>
 * #base_employee
 *
 * @author 乾乾
 * @date 2025/06/09 6:21 下午
 */
public class DefEmployeeMobileCacheKeyBuilder implements CacheKeyBuilder {

    public static CacheKey builder(String mobile) {
        return new DefEmployeeMobileCacheKeyBuilder().key(mobile);
    }

    @Override
    public String getPrefix() {
        return CacheKeyModular.PREFIX;
    }

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public String getTable() {
		return CacheKeyTable.Base.BASE_EMPLOYEE;
    }

    @Override
    public String getModular() {
        return CacheKeyModular.BASE;
    }

    @Override
    public String getField() {
        return "mobile";
    }

    @Override
    public ValueType getValueType() {
        return ValueType.string;
    }

    @Override
    public Duration getExpire() {
        return Duration.ofHours(24);
    }

}
