package com.luohuo.flex.common.cache.common;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 朋友圈权限信息缓存  朋友圈ID -> 朋友圈权限集合
 * @author 乾乾
 */
public class FeedTargetRelCacheKeyBuilder implements CacheKeyBuilder {

	public static CacheHashKey build(Long id) {
		return new FeedTargetRelCacheKeyBuilder().hashKey(id);
	}

	@Override
	public String getTenant() {
		return null;
	}

	@Override
	public String getTable() {
		return CacheKeyTable.Chat.FEED_TARGET;
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
		return Duration.ofDays(30L);
	}
}