package com.luohuo.flex.common.cache.tenant.application;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 应用 KEY
 * [服务模块名:]业务类型[:业务字段][:value类型][:应用id] -> obj
 * tenant:def_application:id:obj:1 -> {}
 * <p>
 * #def_application
 *
 * @author zuihou
 * @date 2020/9/20 6:45 下午
 */
public class ApplicationCacheKeyBuilder implements CacheKeyBuilder {

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public String getPrefix() {
        return CacheKeyModular.PREFIX;
    }

    @Override
    public String getModular() {
        return CacheKeyModular.SYSTEM;
    }

    @Override
    public String getTable() {
        return CacheKeyTable.System.APPLICATION;
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
        return Duration.ofHours(24);
    }
}
