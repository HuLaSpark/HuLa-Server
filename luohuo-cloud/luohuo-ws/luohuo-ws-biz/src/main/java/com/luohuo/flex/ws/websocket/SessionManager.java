package com.luohuo.flex.ws.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.common.cache.FriendCacheKeyBuilder;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.ws.WSOnlineNotify;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.ws.config.ThreadPoolProperties;
import com.luohuo.flex.ws.service.PushService;
import com.luohuo.flex.ws.websocket.nacos.NacosSessionRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 会话管理
 * <p>
 * 线程安全的WebSocket会话生命周期管理器
 * 管理WebSocket会话，包括会话的注册、清理和消息推送。
 * 维护用户会话的路由信息，并在节点下线时迁移会话。
 */
@Slf4j
@Component
public class SessionManager {

	@Resource
	private CachePlusOps cachePlusOps;

	@Resource
	private NacosSessionRegistry nacosSessionRegistry;

	@Resource
	private ThreadPoolProperties threadPoolProperties;
	@Resource
	private PushService pushService;

	// 线程池实例数组（按节点分片）
	private ThreadPoolExecutor[] sessionExecutors;
	// 服务状态 -> 默认可用
	private final AtomicBoolean acceptingNewConnections = new AtomicBoolean(true);

	// Session -> clientId, 通过用户会话反向查找用户设备指纹
	public final ConcurrentHashMap<String, String> SESSION_CLIENT_MAP = new ConcurrentHashMap<>();
	// sessionId -> uid, 通过用户设备指纹反向查找用户id [目前的作用仅仅反向查找会话是哪个uid的]
	public final ConcurrentHashMap<String, Long> SESSION_USER_MAP = new ConcurrentHashMap<>();
	// uid → (clientId → 会话集合) 管理的是单个用户在此服务上所有ws链接，CopyOnWriteArrayList 频繁写入性能较差 所以用Set
	private final ConcurrentHashMap<Long, Map<String, Set<WebSocketSession>>> USER_DEVICE_SESSION_MAP = new ConcurrentHashMap<>();

	public void setAcceptingNewConnections(boolean accepting) {
		acceptingNewConnections.set(accepting);
		log.info("新连接接入状态: {}", accepting);
	}

	public boolean isAcceptingNewConnections() {
		return acceptingNewConnections.get();
	}

	/**
	 * 获取会话数量，[同一个设备可能有多个会话]
	 */
	public int getSessionCount() {
		return USER_DEVICE_SESSION_MAP.values().stream()
				.flatMap(deviceMap -> deviceMap.values().stream())
				.mapToInt(Set::size)
				.sum();
	}

	/**
	 * 获取用户会话
	 */
	public Set<WebSocketSession> getUserSessions(Long uid) {
		Map<String, Set<WebSocketSession>> deviceMap = USER_DEVICE_SESSION_MAP.get(uid);
		if (deviceMap == null) return Collections.emptySet();

		return deviceMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
	}

	/**
	 * 获取所有客户端ID
	 */
	public List<String> getClientIds() {
		return USER_DEVICE_SESSION_MAP.values().stream()
				.flatMap(deviceMap -> deviceMap.keySet().stream())
				.collect(Collectors.toList());
	}

	// 注册会话
	public void registerSession(WebSocketSession session, String clientId, Long uid) {
		// 1. 设备级会话注册
		USER_DEVICE_SESSION_MAP.compute(uid, (key, deviceMap) -> {
			if (deviceMap == null) deviceMap = new ConcurrentHashMap<>();
			deviceMap.compute(clientId, (k, sessions) -> {
				if (sessions == null) sessions = new HashSet<>();
				sessions.add(session);
				return sessions;
			});
			return deviceMap;
		});

		// 2. 反向索引更新 (会话 → 用户+设备)
		SESSION_USER_MAP.put(session.getId(), uid);
		SESSION_CLIENT_MAP.put(session.getId(), clientId);

		// 3. 首次连接设备时触发路由注册
		boolean isFirstSession = USER_DEVICE_SESSION_MAP.get(uid).compute(clientId, (k, sessions) -> {
			if (sessions == null) {
				sessions = new HashSet<>();
				sessions.add(session);
				return sessions;
			}
			sessions.add(session);
			return sessions;
		}).size() == 1;

		if (isFirstSession) {
			nacosSessionRegistry.addUserRoute(uid, clientId);
			syncOnline(uid, clientId, true); // 同步设备在线状态
			log.info("会话注册: clientId={}, uid={},  客户端映射={}, 用户会话={}", clientId, uid, getClientNum(uid, clientId), getUserSessions(uid).size());
		} else {
			log.info("新增会话: clientId={}, uid={}, 当前客户端映射会话数={}, 用户会话={}", clientId, uid, getClientNum(uid, clientId), getUserSessions(uid).size());
		}
	}

	private int getClientNum(Long uid, String clientId) {
		Map<String, Set<WebSocketSession>> map = USER_DEVICE_SESSION_MAP.get(uid);
		if(MapUtil.isEmpty(map)) return 0;

		Set<WebSocketSession> sessions = map.get(clientId);
		if(CollUtil.isEmpty(sessions)) return 0;

		return sessions.size();
	}

	/**
	 * 查询其它设备是否在线
	 * @param uid 当前用户id
	 * @param excludeDeviceKey 要排除的指纹
	 * @return 没设备在线返回true 有设备在线返回false
	 */
	private boolean isFirstOrLastDevice(Long uid, String excludeDeviceKey) {
		String onlineDevicesKey = PresenceCacheKeyBuilder.globalOnlineDevicesKey().getKey();
		String prefix = uid + ":";

		// 分批获取设备
		int batchSize = 1000;
		long total = cachePlusOps.zCard(onlineDevicesKey);

		for (int i = 0; i < total; i += batchSize) {
			Set<Object> batchDevices = cachePlusOps.zRangeByScoreWithScores(
							onlineDevicesKey,
							Double.MIN_VALUE,
							Double.MAX_VALUE,
							i,
							i + batchSize
					).stream()
					.map(ZSetOperations.TypedTuple::getValue)
					.collect(Collectors.toSet());

			for (Object deviceObj : batchDevices) {
				String device = deviceObj.toString();
				if (device.startsWith(prefix) && !device.equals(excludeDeviceKey)) {
					return false; // 发现其他设备
				}
			}
		}
		return true;
	}

	/**
	 * 更新群主在线状态
	 * @param roomIds 房间id
	 * @param uid 用户id
	 * @param online 在线状态
	 */
	private void updateGroupPresence(List<Long> roomIds, Long uid, boolean online) {
		if (CollUtil.isEmpty(roomIds)) return;

		// 2. 批量更新群组在线状态
		roomIds.forEach(roomId -> {
			CacheKey onlineGroupKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomId);
			if (online) {
				cachePlusOps.sAdd(onlineGroupKey, uid);
			} else {
				cachePlusOps.sRem(onlineGroupKey, uid);
			}
		});

		// 3. 更新用户群组在线映射
		CacheKey onlineUserGroupsKey = PresenceCacheKeyBuilder.onlineUserGroupsKey(uid);
		if (online) {
			roomIds.forEach(roomId -> cachePlusOps.sAdd(onlineUserGroupsKey, roomId));
		} else {
			roomIds.forEach(roomId -> cachePlusOps.sRem(onlineUserGroupsKey, roomId));
		}
	}

	/**
	 * 获取用户所有的群聊
	 * @param uid
	 * @return
	 */
	private List<Long> getRoomIds(Long uid) {
		CacheKey ugKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
		return cachePlusOps.sMembers(ugKey).stream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toList());
	}

	/**
	 * 1. 同步在线状态, 即使节点宕机也会被NodeDownMessageListener监听到，从而触发下线
	 * 2. 用户上线之后需要主动推送给用户所有在线好友 + 用户所在群的所有在线成员。如若采用实时轮询的方式会有更多的无效请求!
	 *
	 * @param uid    用户id
	 * @param online 在线状态
	 */
	@RedissonLock(prefixKey = "syncOnline:", key = "#uid")
	public void syncOnline(Long uid, String clientId, boolean online) {
		// 1. 生成用户设备key、全局在线状态key
		String deviceKey = uid + ":" + clientId;
		String onlineDevicesKey = PresenceCacheKeyBuilder.globalOnlineDevicesKey().getKey();
		String onlineUsersKey = PresenceCacheKeyBuilder.globalOnlineUsersKey().getKey();

		// 2. 获取用户所有群组
		List<Long> roomIds = getRoomIds(uid);

		// 2. 检查设备状态（原子操作）
		boolean noOtherDevices = isFirstOrLastDevice(uid, deviceKey);

		if (online) {
			// 3. 上线逻辑
			long millis = System.currentTimeMillis();
			cachePlusOps.zAdd(onlineDevicesKey, deviceKey, millis);
			// 仅仅是首个设备登录时才添加用户在线状态
			if (noOtherDevices) {
				cachePlusOps.zAdd(onlineUsersKey, uid, millis);
				updateGroupPresence(roomIds, uid, true);
				pushDeviceStatusChange(roomIds, uid, clientId, WSRespTypeEnum.ONLINE.getType(), onlineUsersKey);
			}
		} else {
			// 4. 下线逻辑
			cachePlusOps.zRemove(onlineDevicesKey, deviceKey);

			// 所有设备都下线之后移除用户的在线状态
			if (noOtherDevices) {
				cachePlusOps.zRemove(onlineUsersKey, uid);
				updateGroupPresence(roomIds, uid, false);
				pushDeviceStatusChange(roomIds, uid, clientId, WSRespTypeEnum.OFFLINE.getType(), onlineUsersKey);
			}
		}
	}

	/**
	 * 通知所有与自己有关的所有人
	 * @param uid 登录用户
	 * @param clientId 登录设备
	 * @param type 登录状态
	 * @param onlineKey 全局在线用户的key
	 */
	private void pushDeviceStatusChange(List<Long> roomIds, Long uid, String clientId, String type, String onlineKey) {
		// 1. 获取反向好友列表（需要知道该用户在线状态的uid） [推送数据各个不一致]
		CacheKey reverseFriendsKey = FriendCacheKeyBuilder.reverseFriendsKey(uid);
		Set<Long> friends = cachePlusOps.sMembers(reverseFriendsKey).stream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toSet());

		for (Long friendUid : friends) {
			// 1.0 获取该好友的全部好友列表
			CacheKey friendsKey = FriendCacheKeyBuilder.userFriendsKey(friendUid);
			List<Long> hisFriends = cachePlusOps.sMembers(friendsKey).stream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toList());

			// 1.1 管道批量查询分数
			List<Object> scores = cachePlusOps.getZSetScores(onlineKey, hisFriends);

			// 1.2 计算当前好友的在线数量
			long onlineCount = scores.stream().filter(Objects::nonNull).count();

			// 1.3 构建推送消息
			WsBaseResp resp = new WsBaseResp();
			resp.setType(type);
			resp.setData(new WSOnlineNotify(uid, clientId, TimeUtils.getTime(), onlineCount, 2));

			// 1.5 定向推送
			pushService.sendPushMsg(resp, friendUid, uid);
		}

		List<String> keys = roomIds.stream().map(id -> PresenceCacheKeyBuilder.onlineGroupMembersKey(id).getKey()).collect(Collectors.toList());
		List<Long> counts = cachePlusOps.sMultiCard(keys);
		Map<Long, Long> result = new HashMap<>();
		for (int i = 0; i < roomIds.size(); i++) {
			Long count = counts.get(i);
			if (count > 0) {
				result.put(roomIds.get(i), count);
			}
		}

		// 2.1 推送给用户的所有群
		for (Long roomId : result.keySet()) {
			CacheKey cacheKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomId);

			// 2.2 拿到在线群成员
			List<Long> memberIdList = cachePlusOps.sMembers(cacheKey).stream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toList());

			WsBaseResp resp = new WsBaseResp();
			resp.setType(type);
			resp.setData(new WSOnlineNotify(roomId, uid, clientId, TimeUtils.getTime(), result.get(roomId), 1));

			// 2.3 定向分批发送
			int pageSize = 200;
			for (int i = 0; i < memberIdList.size(); i += pageSize) {
				List<Long> page = memberIdList.subList(i, Math.min(i+pageSize, memberIdList.size()));
				pushService.sendPushMsg(resp, page, uid);
			}
		}
	}

	/**
	 * 精确推送到当前服务上的连接
	 * @param uid 要推送的用户id
	 * @param clientId 当前用户的指纹
	 * @param resp 消息内容
	 */
	public Mono<Void> sendToDevice(Long uid, String clientId, WsBaseResp<?> resp) {
		return Mono.defer(() -> {
			Map<String, Set<WebSocketSession>> deviceMap = USER_DEVICE_SESSION_MAP.get(uid);
			if (deviceMap == null) return Mono.empty();

			Set<WebSocketSession> sessions = deviceMap.get(clientId);
			if (CollUtil.isEmpty(sessions)) return Mono.empty();

			return Flux.fromIterable(sessions)
					.filter(WebSocketSession::isOpen)
					.flatMap(session ->
							session.send(Mono.just(session.textMessage(JSONUtil.toJsonStr(resp)))
									.onErrorResume(e -> {
										log.error("发送失败: uid={}, clientId={}, sessionId={}", uid, clientId, session.getId(), e);
										return Mono.empty();
									})// 将 Mono<Void> 转为 Mono<Void>
							))
					.then();
		});
	}

	/**
	 * 清空所有会话
	 */
	public void clean() {
		// 0. 标记服务不可用状态
		setAcceptingNewConnections(false);
		nacosSessionRegistry.deregisterNode();

		// 1. 收集所有设备信息
		Map<Long, Set<String>> offlineDevices = new HashMap<>();
		USER_DEVICE_SESSION_MAP.forEach((uid, deviceMap) -> offlineDevices.put(uid, new HashSet<>(deviceMap.keySet())));

		// 2. 批量关闭会话 + 等待完成（超时控制）
		List<Mono<Void>> closeTasks = USER_DEVICE_SESSION_MAP.values().stream()
				.flatMap(deviceMap -> deviceMap.values().stream())
				.flatMap(Collection::stream)
				.filter(WebSocketSession::isOpen)
				.map(session ->
						session.close(CloseStatus.GOING_AWAY)
								.timeout(Duration.ofSeconds(3)) // 超时控制
								.onErrorResume(e -> {
									log.warn("强制关闭会话失败: {}", session.getId(), e);
									return Mono.empty();
								})
				)
				.collect(Collectors.toList());

		Mono.when(closeTasks).block(Duration.ofSeconds(10)); // 阻塞等待最多10秒

		// 3. 同步清理所有设备状态
		offlineDevices.forEach((uid, clientIds) -> clientIds.forEach(clientId -> syncOnline(uid, clientId, false)));

		// 5. 清空本地映射
		SESSION_USER_MAP.clear();
		SESSION_CLIENT_MAP.clear();
		USER_DEVICE_SESSION_MAP.clear();

		// 6. 清理路由与节点
		nacosSessionRegistry.cleanupNodeRoutes("");
	}

	/**
	 * 原子化清理设备指纹级会话
	 *
	 * @param uid       用户id
	 * @param clientId  用户指纹
	 * @param sessionId 当前绑定的会话
	 * @return
	 */
	private boolean cleanDeviceSession(Long uid, String clientId, String sessionId) {
		AtomicBoolean isLastSession = new AtomicBoolean(false);
		USER_DEVICE_SESSION_MAP.compute(uid, (u, deviceMap) -> {
			if (deviceMap == null) return null;
			deviceMap.compute(clientId, (c, sessions) -> {
				if (sessions != null) {
					sessions.removeIf(s -> s.getId().equals(sessionId));
					if (sessions.isEmpty()) {
						// 标记为最后会话
						isLastSession.set(true);
						// 移除设备条目
						return null;
					}
				}
				return sessions;
			});
			return deviceMap.isEmpty() ? null : deviceMap;
		});
		return isLastSession.get();
	}

	/**
	 * 清理会话
	 * @param session 当前会话
	 */
	public void cleanupSession(WebSocketSession session) {
		if (session != null && !session.isOpen()) {
			session.close(CloseStatus.GOING_AWAY)
					.subscribeOn(Schedulers.boundedElastic())
					.doAfterTerminate(() -> {
						String sessionId = session.getId();

						// 1. 获取反向索引
						String clientId = SESSION_CLIENT_MAP.remove(sessionId);
						Long uid = SESSION_USER_MAP.remove(sessionId);

						if (clientId != null && uid != null) {
							// 2. 原子化清理设备指纹级核心映射
							boolean isLastSession = cleanDeviceSession(uid, clientId, sessionId);

							// 3. 若设备无会话，清理路由
							if (isLastSession) {
								nacosSessionRegistry.removeDeviceRoute(uid, clientId);
								syncOnline(uid, clientId, false); // 通知下线
							}

							Set<WebSocketSession> clientSessions = Optional.ofNullable(USER_DEVICE_SESSION_MAP.get(uid)).map(deviceMap -> deviceMap.get(clientId)).orElse(Collections.emptySet());
							Set<WebSocketSession> sessions = getUserSessions(uid);
							log.info("清理会话: sessionId={}, clientId={}, uid={}, 客户端映射={}, 用户会话={}", sessionId, clientId, uid, CollUtil.isEmpty(clientSessions) ? 0 : clientSessions.size(), CollUtil.isEmpty(sessions) ? 0 : sessions.size());
						}
					})
					.doOnSuccess(v -> log.debug("会话关闭成功: {}", session.getId()))
					.doOnError(e -> log.error("会话关闭失败", e)).subscribe();
		}
	}

	@PostConstruct
	public void init() {
		int poolSize = Math.min(32, Math.max(1, Runtime.getRuntime().availableProcessors() * 2));
		sessionExecutors = new ThreadPoolExecutor[poolSize];

		for (int i = 0; i < poolSize; i++) {
			sessionExecutors[i] = new ThreadPoolExecutor(
					threadPoolProperties.getCoreSize(),  // 核心线程数
					threadPoolProperties.getMaxSize(),    // 最大线程数
					threadPoolProperties.getKeepAlive(), TimeUnit.SECONDS,
					new LinkedBlockingQueue<>(threadPoolProperties.getQueueCapacity()),
					new ThreadFactoryBuilder().setNameFormat("session-pool-" + i + "-%d").build(),
					new ThreadPoolExecutor.CallerRunsPolicy()  // 队列满时由调用线程执行
			);
		}
	}

	@PreDestroy
	public void destroy() {
		log.info("关闭会话管理线程池...");
		Arrays.stream(sessionExecutors).forEach(executor -> {
			executor.shutdown();
			try {
				if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
					List<Runnable> dropped = executor.shutdownNow();
					log.warn("强制关闭线程池, 丢弃任务数: {}", dropped.size());
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		});
	}
}