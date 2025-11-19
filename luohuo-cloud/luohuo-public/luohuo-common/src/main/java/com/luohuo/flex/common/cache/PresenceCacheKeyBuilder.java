package com.luohuo.flex.common.cache;

import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.StrPool;

import java.time.Duration;

/**
 * 在线状态缓存键构建器
 * [前缀:][租户ID:][服务模块名:]业务类型[:业务字段][:value类型][:业务值]
 */
public class PresenceCacheKeyBuilder implements CacheKeyBuilder {

	/**
	 * 全局在线用户ZSet键
	 */
	public static CacheKey globalOnlineUsersKey() {
        return new GlobalOnlineUsersKeyBuilder().key();
    }

	/**
	 * 群里面有多少个用户在线
	 * @param roomId 房间号
	 */
	public static CacheKey onlineGroupMembersKey(Object roomId) {
		return new OnlineGroupMembersKeyBuilder().key(roomId);
	}

	/**
	 * 用户在多少个群里面在线
	 * @param uid 用户id
	 */
	public static CacheKey onlineUserGroupsKey(Long uid) {
		return new OnlineUserGroupsKeyBuilder().key(uid);
	}

	/**
	 * 群里面有多少个用户
	 * @param roomId 房间id
	 */
	public static CacheKey groupMembersKey(Object roomId) {
		return new GroupMembersKeyBuilder().key(roomId);
	}

	/**
	 * 用户有多少个群组
	 * @param uid 用户id
	 */
	public static CacheKey userGroupsKey(Long uid) {
		return new UserGroupsKeyBuilder().key(uid);
	}

	@Override
	public String getTenant() {
		return null;
	}

	@Override
	public String getTable() {
		return CacheKeyTable.Base.ROLE_RESOURCE;
	}

	@Override
	public String getPrefix() {
		return CacheKeyModular.PREFIX;
	}

	@Override
	public String getModular() {
		return CacheKeyModular.BASE;
	}

	@Override
	public String getField() {
		return SuperEntity.ID_FIELD;
	}

	@Override
	public Duration getExpire() {
		return Duration.ofDays(30);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.number;
	}

	public static CacheKey globalOnlineDevicesKey() {
		return new GlobalOnlineDevicesKeyBuilder().key();
	}

	/**
	 * 系统在线的所有用户 uid:clientId 的映射方式
	 */
	private static class GlobalOnlineDevicesKeyBuilder implements CacheKeyBuilder {
		@Override public String getPrefix() { return CacheKeyModular.PREFIX; }
		@Override public String getTenant() { return StrPool.EMPTY; }
		@Override public String getModular() { return CacheKeyModular.PRESENCE; }
		@Override public String getTable() { return CacheKeyTable.Presence.GLOBAL_DEVICES_ONLINE; }
		@Override public ValueType getValueType() { return ValueType.obj; }
		@Override public Duration getExpire() { return Duration.ofDays(30); }
	}

    // 全局在线用户ZSet构建器
    private static class GlobalOnlineUsersKeyBuilder implements CacheKeyBuilder {
        @Override public String getPrefix() {
            return CacheKeyModular.PREFIX;
        }
        @Override public String getTenant() {
            return StrPool.EMPTY;
        }
        @Override public String getModular() {
            return CacheKeyModular.PRESENCE;
        }
        @Override public String getTable() {
            return CacheKeyTable.Presence.GLOBAL_USERS_ONLINE;
        }
        @Override public ValueType getValueType() {
            return ValueType.obj;
        }
        @Override public Duration getExpire() {
            return Duration.ofDays(30); // 需要跟token单词在线时长一致
        }
    }

    // 群组成员构建器
    private static class GroupMembersKeyBuilder implements CacheKeyBuilder {
        @Override public String getPrefix() {
            return CacheKeyModular.PREFIX;
        }
        @Override public String getTenant() {
            return StrPool.EMPTY;
        }
        @Override public String getModular() {
            return CacheKeyModular.PRESENCE;
        }
        @Override public String getTable() {
            return CacheKeyTable.Presence.GROUP_MEMBERS;
        }
        @Override public String getField() {
            return "id";
        }
        @Override public ValueType getValueType() {
            return ValueType.obj;
        }
        @Override public Duration getExpire() {
            return Duration.ofDays(30); // token过期时间+心跳续期
        }
    }

	// 在线群组成员构建器
	private static class OnlineGroupMembersKeyBuilder implements CacheKeyBuilder {
		@Override public String getPrefix() {
			return CacheKeyModular.PREFIX;
		}
		@Override public String getTenant() {
			return StrPool.EMPTY;
		}
		@Override public String getModular() {
			return CacheKeyModular.PRESENCE;
		}
		@Override public String getTable() {
			return CacheKeyTable.Presence.GROUP_MEMBERS_ONLINE;
		}
		@Override public String getField() {
			return "id";
		}
		@Override public ValueType getValueType() {
			return ValueType.obj;
		}
		@Override public Duration getExpire() {
			return Duration.ofDays(30); // token过期时间+心跳续期
		}
	}

	// 用户群组映射构建器
	private static class UserGroupsKeyBuilder implements CacheKeyBuilder {
		@Override
		public String getPrefix() {
			return CacheKeyModular.PREFIX;
		}
		@Override
		public String getTenant() {
			return StrPool.EMPTY;
		}
		@Override
		public String getModular() {
			return CacheKeyModular.PRESENCE;
		}
		@Override
		public String getTable() {
			return CacheKeyTable.Presence.USERS_GROUP;
		}
		@Override
		public String getField() {
			return "groups";
		}
		@Override
		public ValueType getValueType() {
			return ValueType.number;
		}
		@Override
		public Duration getExpire() {
			return Duration.ofDays(30);
		}
	}

	// 在线用户群组映射构建器
	private static class OnlineUserGroupsKeyBuilder implements CacheKeyBuilder {
		@Override
		public String getPrefix() {
			return CacheKeyModular.PREFIX;
		}
		@Override
		public String getTenant() {
			return StrPool.EMPTY;
		}
		@Override
		public String getModular() {
			return CacheKeyModular.PRESENCE;
		}
		@Override
		public String getTable() {
			return CacheKeyTable.Presence.USERS_GROUP_ONLINE;
		}
		@Override
		public String getField() {
			return "groups";
		}
		@Override
		public ValueType getValueType() {
			return ValueType.string;
		}
		@Override
		public Duration getExpire() {
			return Duration.ofDays(30);
		}
	}
}