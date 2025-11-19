package com.luohuo.flex.oauth.cache;

import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 二维码状态键示例
 */
public class QrCacheKeyBuilder implements CacheKeyBuilder {

	public static CacheKey builder(String qrId) {
		return new QrCacheKeyBuilder().key(qrId);
	}

	@Override public String getPrefix() { return CacheKeyModular.PREFIX; }
    @Override public String getModular() { return CacheKeyModular.OAUTH; }
    @Override public String getTable() { return CacheKeyTable.OAUTH.QR; }
    @Override public String getField() { return "id"; }
    @Override public ValueType getValueType() { return ValueType.string; }

	@Override
	public Duration getExpire() {
		return Duration.ofSeconds(30);
	}
}