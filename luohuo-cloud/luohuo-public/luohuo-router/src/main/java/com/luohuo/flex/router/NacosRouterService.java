package com.luohuo.flex.router;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.luohuo.basic.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
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
	private final StringRedisTemplate stringRedisTemplate;

	@Autowired
	public NacosRouterService(
			NacosServiceManager nacosServiceManager,
			NacosDiscoveryProperties discoveryProperties,
			StringRedisTemplate stringRedisTemplate
	) {
		this.namingService = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties());
		this.stringRedisTemplate = stringRedisTemplate;
	}

	// 查询用户设备
	public Set<String> getUserDevices(Long uid) {
		String key = "luohuo:router:user-devices:" + uid;
		return stringRedisTemplate.opsForSet().members(key);
	}

	/**
	 * 返回用户设备所在节点
	 * @param uid 用户id
	 * @param clientId 用户指纹
	 * @return
	 */
	public String getDeviceNode(Long uid, String clientId) {
		String key = "luohuo:router:user-device-nodes:" + uid + ":" + clientId;
		Set<String> nodes = stringRedisTemplate.opsForSet().members(key);
		if (nodes == null) return null;

		// 过滤活跃节点
		Set<String> activeNodes = getAllActiveNodes();
		return nodes.stream()
				.filter(activeNodes::contains)
				.findFirst()
				.orElse(null);
	}

	/**
	 * 查询节点上的设备列表
	 * @param nodeId 节点值
	 */
	public Map<Long, List<String>> getDevicesByNode(String nodeId) {
		String key = "luohuo:router:node-devices:" + nodeId;
		return stringRedisTemplate.opsForSet().members(key).stream()
				.map(entry -> entry.split(":"))
				.collect(Collectors.groupingBy(
						parts -> Long.parseLong(parts[0]),
						Collectors.mapping(parts -> parts[1], Collectors.toList())
				));
	}

	public void removeDeviceRoute(Long uid, String clientId, String nodeId) {
		// 1. 删除设备级路由键
		String deviceRouteKey = "luohuo:router:user-device-nodes:" + uid + ":" + clientId;
		stringRedisTemplate.opsForSet().remove(deviceRouteKey, nodeId);

		// 2. 更新节点设备映射
		String nodeKey = "luohuo:router:node-devices:" + nodeId;
		String userDeviceValue = uid + ":" + clientId;
		stringRedisTemplate.opsForSet().remove(nodeKey, userDeviceValue);
	}

	/**
	 * 提取用户的指纹
	 * @return
	 */
	private String extractClientId(String deviceKey, Long uid) {
		String prefix = "luohuo:router:user-device-nodes:" + uid + ":";
		return deviceKey.substring(prefix.length());
	}

	/**
	 * 管道的方式计算用户与节点的关系
	 * @param deviceKeys 用户所有的指纹信息
	 */
	private Map<String, Set<String>> batchGetNodes(Set<String> deviceKeys) {
		List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<?>) connection -> {
			deviceKeys.forEach(key -> connection.sMembers(key.getBytes()));
			return null;
		});

		Map<String, Set<String>> nodeMap = new HashMap<>();
		Iterator<String> keyIter = deviceKeys.iterator();
		for (Object res : results) {
			if (res instanceof Set<?> rawSet) {
				Set<String> nodes = rawSet.stream()
						.map(Object::toString)
						.collect(Collectors.toSet());
				nodeMap.put(keyIter.next(), nodes);
			}
		}
		return nodeMap;
	}

	/**
	 * 计算用户所有的指纹信息
	 * @param uid
	 * @return
	 */
	private Set<String> scanDeviceKeys(Long uid) {
		Set<String> deviceKeys = new HashSet<>();
		String pattern = "luohuo:router:user-device-nodes:" + uid + ":*";
		try (Cursor<String> cursor = stringRedisTemplate.scan(ScanOptions.scanOptions()
				.match(pattern).count(1000).build())) {
			while (cursor.hasNext()) {
				deviceKeys.add(cursor.next());
			}
		} catch (Exception e) {
			log.error("扫描设备键失败: uid={}", uid, e);
		}
		return deviceKeys;
	}

	/**
	 * 聚合节点 → 设备 → 用户映射
	 * @param uids 用户id
	 */
	public Map<String, Map<String, Long>> findNodeDeviceUser(List<Long> uids) {
		Map<Long, Map<String, Set<String>>> userNodes = findUserNodes(uids);

		// 2. 构建三级映射: 节点 → 设备指纹 → 用户ID
		Map<String, Map<String, Long>> nodeDeviceUser = new ConcurrentHashMap<>();

		userNodes.forEach((uid, deviceMap) ->
				deviceMap.forEach((clientId, nodes) ->
						nodes.forEach(node ->
								nodeDeviceUser.computeIfAbsent(node, k -> new ConcurrentHashMap<>())
										.put(clientId, uid)
						)
				)
		);
		return nodeDeviceUser;
	}
	/**
	 * 批量查询用户所在ws服务节点
	 * @param uids
	 * @return
	 */
	public Map<Long, Map<String, Set<String>>> findUserNodes(List<Long> uids) {
		if (CollUtil.isEmpty(uids)) return Collections.emptyMap();

		Map<Long, Map<String, Set<String>>> resultMap = new HashMap<>();
		int batchSize = 5000;
		Lists.partition(uids, batchSize).forEach(batch -> batch.forEach(uid -> {
			// 1. SCAN 安全查询设备Key
			Set<String> deviceKeys = scanDeviceKeys(uid);
			if (CollUtil.isEmpty(deviceKeys)) return;

			// 2. Pipeline 批量获取节点
			Map<String, Set<String>> deviceNodeMap = batchGetNodes(deviceKeys);

			// 3. 按设备聚合结果
			Map<String, Set<String>> clientNodeMap = new HashMap<>();
			deviceKeys.forEach(key -> clientNodeMap.put(extractClientId(key, uid), deviceNodeMap.get(key)));
			resultMap.put(uid, clientNodeMap);
		}));
		return resultMap;
	}

	/**
	 * 获取所有活跃节点
	 */
	public Set<String> getAllActiveNodes() {
		try {
			List<Instance> instances = namingService.getAllInstances("ws-cluster");
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
			return namingService.getAllInstances("ws-cluster").stream()
				.filter(i -> nodeId.equals(i.getMetadata().get("nodeId")))
				.findFirst()
				.orElse(null);
		} catch (NacosException e) {
			throw new BizException("nacos 服务找不到" + e.getMessage());
		}
	}
}