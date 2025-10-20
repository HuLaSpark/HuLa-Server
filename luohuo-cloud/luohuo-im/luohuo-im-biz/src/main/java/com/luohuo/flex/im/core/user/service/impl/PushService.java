package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.service.MQProducer;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.domain.DelayRetryTask;
import com.luohuo.flex.model.entity.dto.NodePushDTO;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.router.NacosRouterService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 消息推送升级版 [结合消息路由]
 * @author 乾乾
 */
@Slf4j
@Service
public class PushService {
    @Resource
    private MQProducer mqProducer;
	@Resource
	private NacosRouterService routerService;
	@Resource
	private CachePlusOps cachePlusOps;
	// 动态计算线程池大小
	private ExecutorService[] NODE_EXECUTORS;

	// 线程乘数
	@Value("${luohuo.thread.multiplier:2}")
	private int threadMultiplier;

	// 最小线程数
	@Value("${luohuo.thread.min:4}")
	private int minThreads;

	@Value("${luohuo.thread.retry.max-count:3}")
	private Integer maxRetryCount;

	@Value("${luohuo.thread.retry.delay-seconds:3}")
	private Integer delaySeconds;

	// 延迟任务线程池
	private ScheduledExecutorService retryScheduler;

	// 重试次数记录
	private final Map<String, AtomicInteger> messageRetryCountMap = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		int poolSize = Math.max(minThreads, Runtime.getRuntime().availableProcessors() * threadMultiplier);
		NODE_EXECUTORS = new ExecutorService[poolSize];
		for (int i = 0; i < poolSize; i++) {
			NODE_EXECUTORS[i] = Executors.newSingleThreadExecutor(
					new ThreadFactoryBuilder()
							.setNameFormat("push-node-" + i)
							.setDaemon(true)
							.build()
			);
		}

		retryScheduler = Executors.newScheduledThreadPool(2,
				new ThreadFactoryBuilder()
						.setNameFormat("push-retry-scheduler")
						.setDaemon(true)
						.build());

		log.info("初始化推送线程池, size={}", poolSize);
	}

	/**
	 * 统一发送入口
	 * @param msg 消息体
	 * @param uids 接收消息的用户
	 * @param cuid 操作人
	 * @param retry 延迟推送 [延迟二次推送判断在途消息是否已经ack]
	 * @return
	 */
	public CompletableFuture<Void> sendAsync(WsBaseResp<?> msg, List<Long> uids, Long hashId, Long cuid, Boolean retry) {
		// 1. 构建三级映射: 节点 → 设备指纹 → 用户ID
		Map<String, Map<String, Long>> nodeDeviceUser  = routerService.findNodeDeviceUser(uids);

		// 2. 按节点+设备批量推送
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		nodeDeviceUser.forEach((nodeId, deviceUserMap) -> futures.add(CompletableFuture.runAsync(
				() -> {
					if (retry) {
						mqProducer.sendMsgWithDelay(MqConstant.PUSH_DELAY_TOPIC, new NodePushDTO(msg, deviceUserMap, hashId, cuid), delaySeconds);
					} else {
						mqProducer.sendMsg(MqConstant.PUSH_TOPIC + nodeId, new NodePushDTO(msg, deviceUserMap, hashId, cuid));
					}
				}, getExecutorForNode(nodeId)
		)));

		// 3. 执行任务
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	/**
	 * 直接发送，单个用户直接推送
	 */
	public void sendPushMsg(WsBaseResp<?> msg, Long uid, Long cuid) {
		sendPushMsg(msg, Arrays.asList(uid), cuid);
	}

	/**
	 * 批量异步发送给 uidList
	 * TODO 这里hashId 需要重新生成
	 */
	public void sendPushMsg(WsBaseResp<?> msg, List<Long> uidList, Long cuid) {
		sendAsync(msg, uidList, 0L, cuid, false)
				.exceptionally(e -> {
					log.error("批量推送失败", e);
					return null;
				});
	}

	/**
	 * 根据节点名称选择线程池, hash 确保节点分布均匀
	 * broadcast：特殊节点标识
	 */
	private final Map<String, ExecutorService> nodeExecutors = new ConcurrentHashMap<>();
	private ExecutorService getExecutorForNode(String nodeId) {
		return nodeExecutors.computeIfAbsent(nodeId, k ->
				Executors.newSingleThreadExecutor(
						new ThreadFactoryBuilder()
								.setNameFormat("push-" + nodeId + "-%d")
								.build()
				)
		);
	}

	@PreDestroy
	public void destroy() {
		log.info("开始关闭推送线程池...");

		// 1. 关闭推送线程池
		Arrays.stream(NODE_EXECUTORS).forEach(executor -> {
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

		// 2. 关闭延迟调度线程池
		if (retryScheduler != null) {
			retryScheduler.shutdown();
			try {
				if (!retryScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
					retryScheduler.shutdownNow();
				}
			} catch (InterruptedException e) {
				retryScheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}

		// 清理重试状态记录
		messageRetryCountMap.clear();
		log.info("推送线程池和重试调度器已关闭");
	}

	/**
	 * 将消息推送给跟我相关的所有房间
	 * @param uid 推送给这个uid相关的所有人员
	 * @param type 前端监听的类型
	 * @param data 推送的数据
	 */
	public void pushRoom(Long uid, String type, Object data) {
		// 1. 获取用户所在群聊ID列表
		CacheKey ugKey = PresenceCacheKeyBuilder.userGroupsKey(uid);
		Set<Long> roomIds = cachePlusOps.sMembers(ugKey).parallelStream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toSet());

		if (roomIds.isEmpty()) {
			return;
		}

		// 2. 创建通用响应对象
		WsBaseResp commonResp = new WsBaseResp();
		commonResp.setType(type);
		commonResp.setData(data);

		// 3. 获取所有群聊的在线成员并去重
		Set<Long> allMemberIds = new ConcurrentHashSet<>();

		// 并行处理每个群聊的在线成员
		roomIds.parallelStream().forEach(roomId -> {
			CacheKey groupKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomId);
			// 检查群是否在线
			if (cachePlusOps.sCard(groupKey) < 1L) return;

			// 获取群成员并添加到总集合中
			cachePlusOps.sMembers(groupKey).stream()
					.map(obj -> Long.parseLong(obj.toString()))
					.forEach(allMemberIds::add);
		});

		// 4. 移除自己，避免给自己发送消息
//		allMemberIds.remove(uid);

		// 5. 分批推送
		int pageSize = 200;
		List<Long> memberIdList = new ArrayList<>(allMemberIds);

		Lists.partition(memberIdList, pageSize).forEach(batch -> sendPushMsg(commonResp, batch, uid));
	}

	/**
	 * 批量推送方法 [重试专用]
	 */
	public void sendPushMsgWithRetry(WsBaseResp<?> msg, List<Long> uidList, Long hashId, Long cuid) {
		sendAsyncWithRetry(msg, uidList, hashId, cuid)
				.exceptionally(e -> {
					log.error("重试的批量推送最终失败", e);
					return null;
				});
	}

	/**
	 * 单用户推送方法 [重试专用]
	 * @param msg 消息实体
	 * @param uid 推送的uid
	 * @param hashId 需要重试的唯一标识
	 * @param cuid 操作人
	 */
	public void sendPushMsgWithRetry(WsBaseResp<?> msg, Long uid, Long hashId, Long cuid) {
		sendPushMsgWithRetry(msg, Arrays.asList(uid), hashId, cuid);
	}

	/**
	 * 发送带重试机制的推送消息
	 */
	public CompletableFuture<Void> sendAsyncWithRetry(WsBaseResp<?> msg, List<Long> uids, Long hashId, Long cuid) {
		return sendAsync(msg, uids, hashId, cuid, true)
				.exceptionally(throwable -> {
					// 首次推送失败，触发延迟重试
					log.warn("消息首次推送失败，触发延迟重试。uidList: {}, Reason: {}", uids, throwable.getMessage());
					scheduleDelayRetry(msg, uids, hashId, cuid, 1, "首次推送失败: " + throwable.getMessage());
					return null;
				});
	}

	/**
	 * 生成消息唯一标识Key
	 */
	private String generateMessageKey(WsBaseResp<?> msg, List<Long> targetUids) {
		String uidListHash = Integer.toHexString(targetUids.hashCode());
		return String.format("retry_%s_%s_%s",
				msg.getType(),
				uidListHash,
				System.currentTimeMillis() / 1000);
	}

	/**
	 * 调度延迟重试
	 */
	private void scheduleDelayRetry(WsBaseResp<?> msg, List<Long> uids, Long hashId, Long cuid, int retryCount, String reason) {
		String messageKey = generateMessageKey(msg, uids);

		// 检查重试次数
		if (retryCount > maxRetryCount) {
			log.error("消息重试已达最大次数[{}]，停止重试。Key: {}, Reason: {}", maxRetryCount, messageKey, reason);
			messageRetryCountMap.remove(messageKey);
			// 可以在这里添加死信队列处理
			return;
		}

		// 记录重试次数
		messageRetryCountMap.put(messageKey, new AtomicInteger(retryCount));

		// 调度延迟重试
		DelayRetryTask retryTask = new DelayRetryTask(msg, uids, cuid, hashId, retryCount, reason);
		retryScheduler.schedule(() -> executeRetry(retryTask, messageKey), delaySeconds, TimeUnit.SECONDS);

		log.info("延迟重试任务已调度。Key: {}, 第{}次重试, 延迟: {}秒", messageKey, retryCount, delaySeconds);
	}
	/**
	 * 执行重试推送
	 */
	private void executeRetry(DelayRetryTask retryTask, String messageKey) {
		try {
			log.info("开始执行延迟重试。Key: {}, 第{}次重试", messageKey, retryTask.getRetryCount());

			// 再次尝试推送
			sendAsync(retryTask.getOriginalMsg(), retryTask.getTargetUids(), retryTask.getHashId(), retryTask.getOperatorUid(), true)
					.whenComplete((result, throwable) -> {
						if (throwable != null) {
							log.warn("第{}次重试推送失败，准备下一次重试。Key: {}",
									retryTask.getRetryCount(), messageKey, throwable);

							scheduleDelayRetry(retryTask.getOriginalMsg(),
									retryTask.getTargetUids(),
									retryTask.getHashId(),
									retryTask.getOperatorUid(), retryTask.getRetryCount() + 1,
									"重试推送失败: " + throwable.getMessage());
						} else {
							messageRetryCountMap.remove(messageKey);
							log.info("第{}次重试推送成功。Key: {}",
									retryTask.getRetryCount(), messageKey);
						}
					});

		} catch (Exception e) {
			log.error("执行延迟重试时发生异常。Key: {}", messageKey, e);
			// 异常情况下继续重试
			scheduleDelayRetry(retryTask.getOriginalMsg(),
					retryTask.getTargetUids(),
					retryTask.getHashId(),
					retryTask.getOperatorUid(),
					retryTask.getRetryCount() + 1,
					"执行异常: " + e.getMessage());
		}
	}

}
