package com.luohuo.flex.common.cache;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.StrPool;

import java.time.Duration;

/**
 * 在途消息状态缓存
 */
public class PassageMsgCacheKeyBuilder implements CacheKeyBuilder {
	public static CacheKey build(Long uid) {
		return new PassageMsgCacheKeyBuilder().key(uid);
	}

	@Override
	public String getTenant() { return StrPool.EMPTY; }

	@Override
	public String getTable() { return CacheKeyTable.Chat.PASSAGE_MSG; }

	@Override
	public String getPrefix() { return CacheKeyModular.PREFIX; }

	@Override
	public String getModular() { return CacheKeyModular.CHAT; }

	@Override
	public String getField() { return SuperEntity.ID_FIELD; }

	@Override
	public Duration getExpire() { return Duration.ofHours(1); }

	@Override
	public ValueType getValueType() { return ValueType.number; }
}