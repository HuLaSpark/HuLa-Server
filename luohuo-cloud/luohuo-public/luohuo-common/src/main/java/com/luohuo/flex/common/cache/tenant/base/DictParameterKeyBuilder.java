package com.luohuo.flex.common.cache.tenant.base;


import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.io.Serializable;

/**
 * 参数 KEY
 * <p>
 * key: lc:def_parameter:tenant:id:string:{id}
 * value: obj
 *
 * <p>
 * #def_parameter
 *
 * @author 乾乾
 * @date 2020/9/20 6:45 下午
 */
public class DictParameterKeyBuilder implements CacheKeyBuilder {
    public static CacheHashKey builder(Serializable id) {
        return new DictParameterKeyBuilder().hashKey(id);
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
        return CacheKeyTable.System.DEF_PARAMETER;
    }

    @Override
    public String getModular() {
        return CacheKeyModular.SYSTEM;
    }

    @Override
    public String getField() {
        return SuperEntity.ID_FIELD;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.string;
    }

}
