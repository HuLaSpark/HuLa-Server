package com.luohuo.flex.ws.cache;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.io.Serializable;
import java.time.Duration;

/**
 * 关闭房间参数 KEY
 *
 * @author 乾乾
 */
public class CloseRoomCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheKey builder(Serializable roomId) {
        return new CloseRoomCacheKeyBuilder().key(roomId);
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
        return CacheKeyTable.Chat.CLOSE_ROOM;
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

	@Override
	public Duration getExpire() {
		return Duration.ofHours(5);
	}
}
