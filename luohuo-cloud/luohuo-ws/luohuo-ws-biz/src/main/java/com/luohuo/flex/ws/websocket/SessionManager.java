package com.luohuo.flex.ws.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.common.cache.FriendCacheKeyBuilder;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.ws.WSOnlineNotify;
import com.luohuo.flex.ws.config.ThreadPoolProperties;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;
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

	// 线程池实例数组（按节点分片）
	private ThreadPoolExecutor[] sessionExecutors;

	// Session -> clientId, 通过用户会话反向查找用户设备指纹
	public final ConcurrentHashMap<String, String> SESSION_CLIENT_MAP = new ConcurrentHashMap<>();
	// sessionId -> uid, 通过用户设备指纹反向查找用户id [目前的作用仅仅反向查找会话是哪个uid的]
	public final ConcurrentHashMap<String, Long> SESSION_USER_MAP = new ConcurrentHashMap<>();
	// uid → (clientId → 会话集合) 管理的是单个用户在此服务上所有ws链接，CopyOnWriteArrayList 频繁写入性能较差 所以用Set
	private final ConcurrentHashMap<Long, Map<String, Set<WebSocketSession>>> USER_DEVICE_SESSION_MAP = new ConcurrentHashMap<>();

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

	/**
	 * 这里有问题
	 * @param uid
	 * @return
	 */
	private boolean isUserFullyOnline(Long uid) {
		String onlineDevicesKey = PresenceCacheKeyBuilder.globalOnlineDevicesKey().getKey();
//		Set<String> deviceKeys = cachePlusOps.zRangeByScore(onlineDevicesKey, uid + ":*");
		Set<ZSetOperations.TypedTuple<Object>> deviceKeys = cachePlusOps.zReverseRangeWithScores(onlineDevicesKey + uid + ":*", 10);
		return !deviceKeys.isEmpty();
	}

	private void updateGroupPresence(Long uid, boolean online) {
		// 1. 获取用户所有群组
		CacheKey userGroupsKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
		Set<Object> groupIds = cachePlusOps.sMembers(userGroupsKey);

		if (CollUtil.isEmpty(groupIds)) return;

		// 2. 批量更新群组在线状态
		groupIds.forEach(groupId -> {
			CacheKey onlineGroupKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(groupId);
			if (online) {
				cachePlusOps.sAdd(onlineGroupKey, uid);
			} else {
				cachePlusOps.sRem(onlineGroupKey, uid);
			}
		});

		// 3. 更新用户群组在线映射
		CacheKey onlineUserGroupsKey = PresenceCacheKeyBuilder.onlineUserGroupsKey(uid);
		if (online) {
			groupIds.forEach(groupId -> cachePlusOps.sAdd(onlineUserGroupsKey, groupId));
		} else {
			groupIds.forEach(groupId -> cachePlusOps.sRem(onlineUserGroupsKey, groupId));
		}
	}


	/**
	 * 1. 同步在线状态, 即使节点宕机也会被NodeDownMessageListener监听到，从而触发下线
	 * 2. 用户上线之后需要主动推送给用户所有在线好友 + 用户所在群的所有在线成员。如若采用实时轮询的方式会有更多的无效请求!
	 *
	 * @param uid    用户id
	 * @param online 在线状态
	 */
	public void syncOnline(Long uid, String clientId, boolean online) {
		// 1. 更新全局设备在线状态
		String deviceKey = uid + ":" + clientId;
		String onlineDevicesKey = PresenceCacheKeyBuilder.globalOnlineDevicesKey().getKey();

		if (online) {
			cachePlusOps.zAdd(onlineDevicesKey, deviceKey, System.currentTimeMillis());
		} else {
			cachePlusOps.zRemove(onlineDevicesKey, deviceKey);
		}

		// 2. 计算用户全局在线状态
		boolean isUserFullyOnline = isUserFullyOnline(uid);
		String onlineUsersKey = PresenceCacheKeyBuilder.globalOnlineUsersKey().getKey();

		// 首次上线、所有设备下线才执行
		if (online) {
			if (!isUserFullyOnline) {
				cachePlusOps.zAdd(onlineUsersKey, uid.toString(), System.currentTimeMillis());
				updateGroupPresence(uid, true);
				pushDeviceStatusChange(uid, clientId, true);
			}
		} else {
			if (isUserFullyOnline) {
				cachePlusOps.zRemove(onlineUsersKey, uid.toString());
				updateGroupPresence(uid, false);
				pushDeviceStatusChange(uid, clientId, false);
			}
		}
	}

	private Set<Long> getRelatedUsers(Long uid) {
		Set<Long> result = new HashSet<>();

		// 1. 获取双向好友关系
		CacheKey reverseFriendsKey = FriendCacheKeyBuilder.reverseFriendsKey(uid);
		Set<Long> friends = cachePlusOps.sMembers(reverseFriendsKey).stream()
				.map(obj -> Long.parseLong(obj.toString()))
				.collect(Collectors.toSet());
		result.addAll(friends);

		// 2. 获取所在群组的成员
		CacheKey userGroupsKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
		List<Long> groupIds = cachePlusOps.sMembers(userGroupsKey).stream()
				.map(obj -> Long.parseLong(obj.toString()))
				.collect(Collectors.toList());

		groupIds.forEach(groupId -> {
			CacheKey groupMembersKey = PresenceCacheKeyBuilder.groupMembersKey(groupId);
			cachePlusOps.sMembers(groupMembersKey).stream()
					.map(obj -> Long.parseLong(obj.toString()))
					.filter(memberId -> !memberId.equals(uid)) // 排除自己
					.forEach(result::add);
		});

		return result;
	}

	/**
	 * 通知所有与自己有关的所有人
	 * @param uid 登录用户
	 * @param clientId 登录设备
	 * @param online 登录状态
	 */
	private void pushDeviceStatusChange(Long uid, String clientId, boolean online) {
		Set<Long> notifyTargets = getRelatedUsers(uid);
		String type = online ? WSRespTypeEnum.ONLINE.getType() : WSRespTypeEnum.OFFLINE.getType();

		// 1. 构建设备状态通知
		WsBaseResp resp = new WsBaseResp();
		resp.setType(type);
		resp.setData(new WSOnlineNotify(uid, clientId, System.currentTimeMillis(), 0L, 2));

		// 2. 批量推送
		int batchSize = 200;
		List<List<Long>> batches = Lists.partition(new ArrayList<>(notifyTargets), batchSize);

		batches.forEach(batch -> {
			int index = Math.abs(batch.hashCode()) % sessionExecutors.length;
			ThreadPoolExecutor executor = sessionExecutors[index];

			// 异步推送避免阻塞
			CompletableFuture.runAsync(() -> batch.forEach(targetUid -> sendToUser(targetUid, resp)), executor);
		});
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
	public void clean(CloseStatus status) {
		// 1. 异步关闭所有活跃会话
		USER_DEVICE_SESSION_MAP.values().parallelStream()
				.flatMap(deviceMap -> deviceMap.values().stream())
				.flatMap(Collection::stream)
				.filter(WebSocketSession::isOpen)
				.forEach(session ->
						session.close(status)
								.doOnError(e -> log.error("关闭会话失败: session={}", session.getId(), e))
								.subscribe()
				);

		// 2. 清空所有映射表
		SESSION_USER_MAP.clear();
		SESSION_CLIENT_MAP.clear();
		USER_DEVICE_SESSION_MAP.clear();

		// 3. 清理Redis中的节点路由数据
		nacosSessionRegistry.cleanupNodeRoutes();

		// 5. 从Nacos注销节点
		nacosSessionRegistry.deregisterNode();
	}

	/**
	 * 推送给本机所有连接 [用不到]
	 * @param resp 消息实体
	 */
//	public Mono<Void> broadcast(WsBaseResp<?> resp) {
//		String jsonPayload = JSONUtil.toJsonStr(resp);
//		return Flux.fromIterable(new ArrayList<>(USER_DEVICE_SESSION_MAP.values()))
//				.flatMap(deviceMap -> Flux.fromIterable(deviceMap.values()))
//				.flatMap(Flux::fromIterable).parallel() // 启用并行流
//				.runOn(Schedulers.parallel()) // 使用并行调度器
//				.filter(WebSocketSession::isOpen)
//				.flatMap(session ->
//						session.send(Mono.just(session.textMessage(jsonPayload)))
//								.onErrorResume(e -> {
//									log.error("广播失败 session={}", session.getId(), e);
//									return Mono.empty();
//								})
//				)
//				.sequential()
//				.then();
//	}

	/**
	 * 推送消息
	 *
	 * @param uid  用户id
	 * @param resp 推送内容
	 * @return
	 */
	public Mono<Void> sendToUser(Long uid, WsBaseResp<?> resp) {
		return Mono.fromCallable(() -> USER_DEVICE_SESSION_MAP.get(uid))
				.flatMapMany(deviceMap -> Flux.fromIterable(deviceMap.values()) // 支持多设备会话
						.flatMap(sessions -> Flux.fromIterable(sessions) // 遍历设备的所有会话
								.filter(session -> {
									// 验证会话有效性
									String sessionId = session.getId();
									String clientId = SESSION_CLIENT_MAP.get(sessionId);
									return session.isOpen() && clientId != null &&
											deviceMap.containsKey(clientId) &&
											deviceMap.get(clientId).contains(session);
								})
								.flatMap(session ->
										session.send(Mono.just(session.textMessage(JSONUtil.toJsonStr(resp))))
												.onErrorResume(e -> {
													log.error("发送失败: uid={}, sessionId={}", uid, session.getId(), e);
													return Mono.empty();
												})
								)
						))
				.then();
//				.doOnSuccess(v -> log.info("消息已发送至用户 {}", uid));
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