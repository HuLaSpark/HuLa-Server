package com.luohuo.flex.common.cache;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.StrPool;
import java.time.Duration;


public class FriendCacheKeyBuilder implements CacheKeyBuilder {

	// 用户主动好友关系缓存（UID -> 好友UID集合）
	public static CacheKey userFriendsKey(Long uid) {
		return new UserFriendsKeyBuilder().key(uid);
	}

	// 反向好友关系缓存（UID -> 关注该用户的UID集合）
	public static CacheKey reverseFriendsKey(Long uid) {
		return new ReverseFriendsKeyBuilder().key(uid);
	}

	// 好友关系状态缓存（UID1_UID2 -> 关系状态）
	public static CacheKey friendStatusKey(Long uid1, Long uid2) {
		Long minUid = Math.min(uid1, uid2);
		Long maxUid = Math.max(uid1, uid2);
		return new FriendStatusKeyBuilder().key(minUid + "_" + maxUid);
	}

	@Override
	public String getTenant() { return StrPool.EMPTY; }

	@Override
	public String getTable() { return CacheKeyTable.Friend.RELATION; }

	@Override
	public String getPrefix() { return CacheKeyModular.PREFIX; }

	@Override
	public String getModular() { return CacheKeyModular.FRIEND; }

	@Override
	public String getField() { return SuperEntity.ID_FIELD; }

	@Override
	public Duration getExpire() { return Duration.ofDays(30); }

	@Override
	public ValueType getValueType() { return ValueType.number; }

	// 主动好友关系键构建器
	private static class UserFriendsKeyBuilder implements CacheKeyBuilder {
		@Override
		public String getPrefix() {
			return CacheKeyModular.PREFIX;
		}

		@Override
		public String getTenant() {
			return null;
		}

		@Override
		public String getModular() {
			return CacheKeyModular.FRIEND;
		}

		@Override public String getTable() {
			return CacheKeyTable.Friend.USER_FRIENDS;
		}
		@Override public ValueType getValueType() {
			return ValueType.number; // Redis Set存储好友UID集合
		}
	}

	// 反向好友关系键构建器
	private static class ReverseFriendsKeyBuilder implements CacheKeyBuilder {
		@Override
		public String getPrefix() {
			return CacheKeyModular.PREFIX;
		}

		@Override
		public String getTenant() {
			return null;
		}

		@Override
		public String getModular() {
			return CacheKeyModular.FRIEND;
		}

		@Override public String getTable() {
			return CacheKeyTable.Friend.REVERSE_FRIENDS;
		}
		@Override public ValueType getValueType() {
			return ValueType.number; // Redis Set存储关注者UID集合
		}
	}

	// 好友关系状态键构建器
	private static class FriendStatusKeyBuilder implements CacheKeyBuilder {
		@Override
		public String getPrefix() {
			return CacheKeyModular.PREFIX;
		}
		@Override
		public String getTenant() {
			return null;
		}

		@Override
		public String getModular() {
			return CacheKeyModular.FRIEND;
		}

		@Override public String getTable() {
			return CacheKeyTable.Friend.RELATION_STATUS;
		}
		@Override public ValueType getValueType() {
			return ValueType.string; // 存储关系状态值（1/0）
		}
		@Override public Duration getExpire() {
			return Duration.ofDays(7); // 状态缓存较短，避免长期不一致
		}
	}
}