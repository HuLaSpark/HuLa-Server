package com.luohuo.flex.ws.cache;

import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.flex.common.cache.CacheKeyModular;

import java.time.Duration;

/**
 * 视频会议参数 KEY
 * <p>
 *
 * @author 乾乾
 * @date 2025/7/28 6:45 下午
 */
public class VideoRoomsCacheKeyBuilder implements CacheKeyBuilder {
	public static CacheKey build(Long key) {
		return new VideoRoomsCacheKeyBuilder().key(key);
	}

	@Override
	public String getPrefix() {
		return CacheKeyModular.PREFIX;
	}

	@Override
	public String getTable() {
		return "rooms";
	}

	@Override public String getModular() {
		return "video";
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
