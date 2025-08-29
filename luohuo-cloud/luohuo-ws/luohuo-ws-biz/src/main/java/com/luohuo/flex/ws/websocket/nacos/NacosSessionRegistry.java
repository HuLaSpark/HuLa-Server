package com.luohuo.flex.ws.websocket.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.router.RouterCacheKeyBuilder;
import com.luohuo.flex.ws.websocket.SessionManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Nacos会话注册中心
 *
 * 负责将当前节点注册到Nacos，并维护用户会话的路由信息。
 * 定时更新节点的心跳和会话信息。
 */
@Slf4j
@Component
public class NacosSessionRegistry {

	// 节点实例缓存
	private Instance nodeInstance;

    private NamingService namingService;

	@Lazy
	@Resource
	private SessionManager sessionManager;

	@Resource
	private CachePlusOps cachePlusOps;

	RedisTemplate<String, Object> redisTemplate;

    // 节点唯一标识
    private String nodeId;

	private String nodeIp;

	private int nodePort;

	@Autowired
	public NacosSessionRegistry(NacosServiceManager nacosServiceManager, RedisTemplate<String, Object> redisTemplate,
			@Value("${luohuo.node-id}") String nodeId, @Value("${server.port}") int nodePort, NacosDiscoveryProperties discoveryProperties) {
		this.redisTemplate = redisTemplate;
		this.nodeId = nodeId;
		this.nodePort = nodePort;

		// 确定节点IP
		this.nodeIp = determineNodeIp(discoveryProperties);

		log.info("节点IP: {}", nodeIp);

		// 创建命名服务实例
		this.namingService = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties());
	}

	private String determineNodeIp(NacosDiscoveryProperties properties) {
		if (StringUtils.hasText(properties.getIp())) {
			return properties.getIp();
		}
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "127.0.0.1";
		}
	}

	@PostConstruct
	public void init() {
		// 初始化节点实例
		nodeInstance = createNodeInstance();
		registerNodeInstance();
	}

	private Instance createNodeInstance() {
		Instance instance = new Instance();
		instance.setIp(nodeIp);
		instance.setPort(nodePort);
		instance.setHealthy(true);
		instance.setEphemeral(true);

		Map<String, String> metadata = new HashMap<>();
		metadata.put("nodeId", nodeId);
		metadata.put("startTime", Instant.now().toString());
		instance.setMetadata(metadata);
		return instance;
	}

	private void registerNodeInstance() {
		try {
			namingService.registerInstance("ws-cluster", "WS_GROUP", nodeInstance);
			log.info("节点注册成功: {}", nodeId);
		} catch (NacosException e) {
			log.error("节点注册失败", e);
		}
	}

	/**
	 * 添加用户路由信息
	 * @param uid 用户id
	 */
	public void addUserRoute(Long uid, String clientId) {
		String deviceField = uid + ":" + clientId;

		// 1. 设备指纹→节点映射
		cachePlusOps.hSet(RouterCacheKeyBuilder.buildDeviceNodeMap(deviceField), nodeId);

		// 2. 节点→设备指纹映射
		cachePlusOps.sAdd(RouterCacheKeyBuilder.buildNodeDevices(nodeId), deviceField);

		// 3. 更新节点元数据
		Map<String, String> metadata = nodeInstance.getMetadata();
		metadata.put("lastActive", Instant.now().toString());
		nodeInstance.setMetadata(metadata);
		log.info("添加用户路由: uid={}, clientId={}, node={}", uid, clientId, nodeId);
	}

	/**
	 * 移除用户路由信息
	 * @param uid 用户id
	 */
	public void removeDeviceRoute(Long uid, String clientId) {
		String deviceField = uid + ":" + clientId;

		// 清理设备→节点映射
		CacheHashKey deviceNodeMap = RouterCacheKeyBuilder.buildDeviceNodeMap(deviceField);
		cachePlusOps.hDel(deviceNodeMap);

		// 清理节点→设备映射
		CacheKey nodeDevices = RouterCacheKeyBuilder.buildNodeDevices(nodeId);
		cachePlusOps.sRem(nodeDevices, deviceField);
	}

	/**
	 * 清理节点在Redis中的所有路由信息
	 */
	public void cleanupNodeRoutes() {
		// 1. 清理节点→设备映射
		CacheKey cacheKey = RouterCacheKeyBuilder.buildNodeDevices(nodeId);
		Set<Object> deviceFields = redisTemplate.opsForSet().members(cacheKey.getKey());

		if (!CollectionUtils.isEmpty(deviceFields)) {
			// 批量删除全局Hash中的映射
			CacheHashKey deviceNodeMap = RouterCacheKeyBuilder.buildDeviceNodeMap("");
			redisTemplate.opsForHash().delete(deviceNodeMap.getKey(), deviceFields.toArray());

			// 清理节点本地映射
			redisTemplate.delete(cacheKey.getKey());
		}
		log.info("节点路由清理完成: nodeId={}, 清理设备数={}", nodeId, deviceFields != null ? deviceFields.size() : 0);
	}

	public void deregisterNode() {
		try {
			namingService.deregisterInstance("ws-cluster", "WS_GROUP", nodeIp, nodePort);
			log.info("Nacos节点注销成功: {}", nodeId);
		} catch (NacosException e) {
			log.error("Nacos节点注销失败", e);
		}
	}
    
    @Scheduled(fixedRate = 5000)
	public void updateNodeMetrics() {
		try {
			// 更新元数据
			Map<String, String> metadata = new HashMap<>(nodeInstance.getMetadata());
			metadata.put("lastHeartbeat", Instant.now().toString());
			metadata.put("sessionCount", String.valueOf(sessionManager.getSessionCount()));

			// 添加客户端ID列表
			List<String> clientIds = sessionManager.getClientIds();
			if (!clientIds.isEmpty()) {
				metadata.put("clientIds", String.join(",", clientIds.subList(0, Math.min(10, clientIds.size()))));
			}

			// 重新注册更新后的实例
			nodeInstance.setMetadata(metadata);
			namingService.registerInstance("ws-cluster", "WS_GROUP", nodeInstance);
		} catch (NacosException e) {
			log.error("心跳更新失败", e);
		}
	}
}