package com.luohuo.flex.router;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 提供与Nacos相关的路由服务，包括节点查询、用户路由信息维护等。
 */
@Slf4j
@Component
public class NacosRouterService {
	private final NamingService namingService;
	RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public NacosRouterService(
			NacosServiceManager nacosServiceManager,
			NacosDiscoveryProperties discoveryProperties,
			RedisTemplate<String, Object> redisTemplate
	) {
		this.namingService = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties());
		this.redisTemplate = redisTemplate;
	}

	// 查询用户设备
	public Set<String> getUserDevices(Long uid) {
		CacheHashKey deviceNodeMap = RouterCacheKeyBuilder.buildDeviceNodeMap(uid + ":*");
		// 扫描全局Hash中属于该用户的设备字段 (uid:*)
		ScanOptions options = ScanOptions.scanOptions().match(uid + ":*").count(100).build();

		Set<String> deviceFields = new HashSet<>();
		try (Cursor<Map.Entry<Object, Object>> cursor =
					 redisTemplate.opsForHash().scan(deviceNodeMap.getKey(), options)) {
			while (cursor.hasNext()) {
				Map.Entry<Object, Object> entry = cursor.next();
				deviceFields.add((String) entry.getKey());
			}
		}

		// 提取设备ID
		return deviceFields.stream().map(field -> field.split(":")[1]).collect(Collectors.toSet());
	}

	/**
	 * 返回用户设备所在节点
	 * @param uid 用户id
	 * @param clientId 用户指纹
	 * @return 节点ID，如果找不到或节点不活跃则返回null
	 */
	public String getDeviceNode(Long uid, String clientId) {
		// 1. 直接从全局Hash中获取设备对应的节点
		String deviceField = uid + ":" + clientId;
		CacheHashKey deviceNodeMap = RouterCacheKeyBuilder.buildDeviceNodeMap(deviceField);
		String nodeId = (String) redisTemplate.opsForHash().get(deviceNodeMap.getKey(), deviceField);

		// 2. 如果节点不存在，直接返回null
		if (nodeId == null) {
			return null;
		}

		// 3. 检查节点是否活跃
		Set<String> activeNodes = getAllActiveNodes();
		return activeNodes.contains(nodeId) ? nodeId : null;
	}

	/**
	 * 查询节点上的设备列表
	 * @param nodeId 节点值
	 */
	public Map<Long, List<String>> getDevicesByNode(String nodeId) {
		CacheKey cacheKey = RouterCacheKeyBuilder.buildNodeDevices(nodeId);
		return redisTemplate.opsForSet().members(cacheKey.getKey()).stream()
				.map(entry -> entry.toString().split(":"))
				.collect(Collectors.groupingBy(
						parts -> Long.parseLong(parts[0]),
						Collectors.mapping(parts -> parts[1], Collectors.toList())
				));
	}

	/**
	 * 移除用户设备路由信息
	 * @param uid 用户ID
	 * @param clientId 设备指纹
	 * @param nodeId 节点ID
	 */
	public void removeDeviceRoute(Long uid, String clientId, String nodeId) {
		// 1. 从全局Hash中删除设备-节点映射
		String deviceField = uid + ":" + clientId;
		CacheHashKey deviceNodeMap = RouterCacheKeyBuilder.buildDeviceNodeMap(deviceField);
		redisTemplate.opsForHash().delete(deviceNodeMap.getKey(), deviceField);

		// 2. 从节点设备集合中删除设备标识
		CacheKey cacheKey = RouterCacheKeyBuilder.buildNodeDevices(nodeId);
		redisTemplate.opsForSet().remove(cacheKey.getKey(), deviceField);

		log.debug("移除设备路由: uid={}, clientId={}, nodeId={}", uid, clientId, nodeId);
	}

	/**
	 * 聚合节点 → 设备 → 用户映射
	 * @param uids 用户id
	 */
	public Map<String, Map<String, Long>> findNodeDeviceUser(List<Long> uids) {
		// 0. 前置校验
		if (CollUtil.isEmpty(uids)) return Collections.emptyMap();

		// 1. 提取目标UID集合
		Set<Long> targetUids = new HashSet<>(uids);

		// 2. 获取全局设备-节点映射（改用HSCAN分批加载）
		CacheHashKey deviceNodeMap = RouterCacheKeyBuilder.buildDeviceNodeMap("");
		Map<String, Map<String, Long>> result = new ConcurrentHashMap<>();

		// 3. 过滤活跃节点
		Set<String> activeNodes = getAllActiveNodes();

		// 5. 使用HSCAN游标分批遍历
		ScanOptions options = ScanOptions.scanOptions().count(500).build();
		try (Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(deviceNodeMap.getKey(), options)) {
			while (cursor.hasNext()) {
				Map.Entry<Object, Object> entry = cursor.next();

				// 5.1 直接使用字符串类型
				String field = (String) entry.getKey();
				String nodeId = (String) entry.getValue();
				if (!activeNodes.contains(nodeId)) continue;

				// 5.2 按uid过滤目标用户
				String[] parts = field.split(":");
				if (parts.length != 2) continue;

				Long uid = Long.parseLong(parts[0]);
				String clientId = parts[1];

				// 5.3 判断是否是推送数据
				if (!targetUids.contains(uid)) continue;

				// 5.5 构建映射：节点 → 设备 → UID
				result.computeIfAbsent(nodeId, k -> new ConcurrentHashMap<>()).put(clientId, uid);
			}
		}
		return result;
	}

	/**
	 * 获取所有活跃节点
	 */
	public Set<String> getAllActiveNodes() {
		try {
			List<Instance> instances = namingService.getAllInstances("ws-cluster", "WS_GROUP");
			return instances.stream()
					.filter(Instance::isHealthy)
					.map(instance -> instance.getMetadata().get("nodeId"))
					.collect(Collectors.toSet());
		} catch (NacosException e) {
			throw new BizException("获取节点列表失败", e);
		}
	}

    // 获取节点详情
    public Instance getNodeDetail(String nodeId) {
		try {
			return namingService.getAllInstances("ws-cluster", "WS_GROUP").stream()
				.filter(i -> nodeId.equals(i.getMetadata().get("nodeId")))
				.findFirst()
				.orElse(null);
		} catch (NacosException e) {
			throw new BizException("nacos 服务找不到" + e.getMessage());
		}
	}
}