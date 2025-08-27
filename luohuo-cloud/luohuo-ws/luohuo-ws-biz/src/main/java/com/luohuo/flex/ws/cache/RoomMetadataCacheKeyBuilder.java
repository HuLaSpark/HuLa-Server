package com.luohuo.flex.ws.cache;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

/**
 * 房间元数据服务参数 KEY
 *
 * @author 乾乾
 */
public class RoomMetadataCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheHashKey builder(Long roomId, String itemKey) {
        return new RoomMetadataCacheKeyBuilder().hashFieldKey(itemKey, roomId);
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
        return CacheKeyTable.Chat.ROOM_META;
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
        return ValueType.string;
    }
}
