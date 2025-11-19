package com.luohuo.flex.router;

import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;

import java.time.Duration;

/**
 * 路由模块缓存键构建器
 */
public class RouterCacheKeyBuilder {
	public static CacheHashKey buildDeviceNodeMap(String clientId) {
		return new DeviceNodeMapping().hashFieldKey(clientId, "device-node-mapping");
	}

	public static CacheKey buildNodeDevices(String nodeId) {
		return new NodeDevices().key(nodeId);
	}

	/**
	 * 设备-节点映射表
	 */
    public static class DeviceNodeMapping implements CacheKeyBuilder {
		@Override
		public String getPrefix() {
			return "luohuo";
		}

		@Override
		public String getTenant() {
			return null;
		}

        @Override
        public String getTable() {
            return "router";
        }

		@Override
		public ValueType getValueType() {
			return ValueType.string;
		}

		@Override
        public Duration getExpire() {
			return Duration.ofSeconds(-1);
        }
    }

	/**
	 * 节点设备集合
	 */
	public static class NodeDevices implements CacheKeyBuilder {
		@Override
		public String getPrefix() {
			return "luohuo";
		}

		@Override
		public String getTenant() {
			return null;
		}

		@Override
		public String getModular() {
			return "router";
		}

        @Override
        public String getTable() {
            return "node-devices";
        }

		@Override
		public ValueType getValueType() {
			return ValueType.string;
		}

        @Override
        public Duration getExpire() {
            return Duration.ofSeconds(-1);
        }
    }
}