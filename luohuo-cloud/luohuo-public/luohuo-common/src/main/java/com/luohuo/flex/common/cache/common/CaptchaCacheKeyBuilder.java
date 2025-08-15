package com.luohuo.flex.common.cache.common;


import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 参数 KEY
 * <p>
 *
 * @author zuihou
 * @date 2020/9/20 6:45 下午
 */
public class CaptchaCacheKeyBuilder implements CacheKeyBuilder {
	public static CacheHashKey hashBuild(String key, String template, Long expireTime) {
		CacheHashKey hashKey = new CaptchaCacheKeyBuilder().hashFieldKey(key, template);
		hashKey.setExpire(Duration.ofSeconds(expireTime));
		return hashKey;
	}

	public static CacheHashKey hashBuild(String key, String template) {
		return new CaptchaCacheKeyBuilder().hashFieldKey(key, template);
	}

    public static CacheKey build(String key, String template) {
        return new CaptchaCacheKeyBuilder().key(key, template);
    }

    @Override
    public String getTable() {
        return CacheKeyTable.CAPTCHA;
    }

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public Duration getExpire() {
        return Duration.ofMinutes(15);
    }
}
