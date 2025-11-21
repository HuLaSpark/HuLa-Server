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
 * key: lc:def_dict:tenant:id:string:{dict_key}
 * field1: {item_key1} --> item_name
 * field2: {item_key2} --> item_name
 *
 * <p>
 * #def_dict
 *
 * @author 乾乾
 * @date 2020/9/20 6:45 下午
 */
public class DictCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheHashKey builder(Serializable dictKey) {
        return new DictCacheKeyBuilder().hashKey(dictKey);
    }

    public static CacheHashKey builder(String dictKey, String itemKey) {
        return new DictCacheKeyBuilder().hashFieldKey(itemKey, dictKey);
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
        return CacheKeyTable.System.DICT;
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
