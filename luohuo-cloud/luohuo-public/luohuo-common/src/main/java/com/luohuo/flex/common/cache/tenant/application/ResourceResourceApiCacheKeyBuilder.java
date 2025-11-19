package com.luohuo.flex.common.cache.tenant.application;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.StrPool;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 资源的资源接口 KEY
 * [服务模块名:]业务类型[:业务字段][:value类型][:资源id] -> []
 * tenant:def_resource.def_resource_api:id.id:number:1 -> [apiId]
 * <p>
 * #def_resource_api
 *
 * @author 乾乾
 * @date 2020/9/20 6:45 下午
 */
public class ResourceResourceApiCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheKey builder(Long resourceId) {
        return new ResourceResourceApiCacheKeyBuilder().key(resourceId);
    }

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
        return StrUtil.join(StrPool.DOT, CacheKeyTable.System.RESOURCE, CacheKeyTable.System.RESOURCE_API);
    }

    @Override
    public String getField() {
        return StrUtil.join(StrPool.DOT, SuperEntity.ID_FIELD, SuperEntity.ID_FIELD);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.number;
    }

    @Override
    public Duration getExpire() {
        return Duration.ofHours(24);
    }
}
