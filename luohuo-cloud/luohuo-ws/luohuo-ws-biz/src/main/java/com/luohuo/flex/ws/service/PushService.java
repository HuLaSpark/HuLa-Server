package com.luohuo.flex.ws.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.dto.NodePushDTO;
import com.luohuo.flex.router.NacosRouterService;
import com.luohuo.flex.ws.config.ThreadPoolProperties;
import com.luohuo.flex.ws.websocket.SessionManager;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 消息推送升级版 [结合消息路由] ws服务专用
 * @author 乾乾
 */
@Slf4j
@Service
public class PushService {
	@Resource
	private ThreadPoolProperties threadPoolProperties;
	@Resource
	private RocketMQTemplate rocketMQTemplate;
	@Resource
	private NacosRouterService routerService;
	@Resource@Lazy
	private SessionManager sessionManager;

	@Value("${luohuo.node-id}")
	private String nodeId;

	// 本地推送调度器
	private final Scheduler localPushScheduler = Schedulers.newBoundedElastic(50, 1000, "local-push");

	/**
	 * 单用户推送
	 * @param msg 消息内容
	 * @param uid 目标用户ID
	 * @param cuid 操作人ID
	 */
	public void sendPushMsg(WsBaseResp<?> msg, Long uid, Long cuid) {
		sendPushMsg(msg, Arrays.asList(uid), cuid);
	}

	/**
	 * 将消息推送到对应的用户
	 * @param msg 消息内容
	 * @param uidList 目标用户ID列表
	 * @param cuid 操作人ID
	 */
	public void sendPushMsg(WsBaseResp<?> msg, List<Long> uidList, Long cuid) {
		sendAsync(msg, uidList, cuid).exceptionally(ex -> {
			log.error("批量推送失败: uids={}", uidList, ex);
			return null;
		});
	}

	/**
	 * 统一发送入口
	 * @param msg 消息体
	 * @param uids 接收消息的用户
	 * @param cuid 操作人
	 */
	public CompletableFuture<Void> sendAsync(WsBaseResp<?> msg, List<Long> uids, Long cuid) {
		// 1. 构建三级映射: 节点 → 设备指纹 → 用户ID
		Map<String, Map<String, Long>> nodeDeviceUser  = routerService.findNodeDeviceUser(uids);

		// 2. 并行发送到所有节点
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		nodeDeviceUser.forEach((nodeId, deviceUserMap) -> {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				if (this.nodeId.equals(nodeId)) {
					// 本地节点直接推送
					localPush(deviceUserMap, msg);
				} else {
					// 远程节点通过MQ转发
					sendToNodeViaMQ(nodeId, msg, deviceUserMap, cuid);
				}
			}, getExecutorForNode(nodeId));
			futures.add(future);
		});

		// 3. 执行
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	/**
	 * 本地节点直接推送
	 * @param deviceUserMap 指纹与用户数据
	 */
	private void localPush(Map<String, Long> deviceUserMap, WsBaseResp<?> msg) {
		// 1. 按设备数动态调整并行度, 最大32 线程并发
		int parallelism = Math.min(deviceUserMap.size(), 32);

		Flux.fromIterable(deviceUserMap.entrySet())
				.parallel(parallelism)
				.runOn(localPushScheduler)
				.flatMap(entry ->
						// 2. 增加超时中断机制
						sessionManager.sendToDevice(entry.getValue(), entry.getKey(), msg)
								.timeout(Duration.ofSeconds(5))
								.onErrorResume(e -> {
									log.error("设备推送超时: {}", entry.getKey());return Mono.empty();})
				).subscribe(null, e -> log.error("本地批量推送失败", e),
						() -> log.debug("本地推送完成: {}设备", deviceUserMap.size()));
	}

	/**
	 * 将消息推送到指定节点
	 * @param nodeId 节点信息
	 * @param deviceUserMap 指纹与用户数据
	 * @param msg 消息内容
	 * @param cuid 操作人
	 */
	private void sendToNodeViaMQ(String nodeId, WsBaseResp<?> msg, Map<String, Long> deviceUserMap, Long cuid) {
		try {
			// TODO 这里要解决一下唯一标识的问题
			rocketMQTemplate.send(MqConstant.PUSH_TOPIC + nodeId, MessageBuilder.withPayload(new NodePushDTO(msg, deviceUserMap, cuid, cuid)).build());
		} catch (Exception e) {
			log.error("MQ转发失败: node={}, devices={}", nodeId, deviceUserMap.keySet(), e);
		}
	}

	private final Map<String, ThreadPoolExecutor> nodeExecutors = new ConcurrentHashMap<>();
	/**
	 * 根据节点ID获取专属线程池
	 * @param nodeId 节点标识
	 * @return 线程池实例、有界队列防OOM
	 */
	private ExecutorService getExecutorForNode(String nodeId) {
		return nodeExecutors.computeIfAbsent(nodeId, k ->
				new ThreadPoolExecutor(
						threadPoolProperties.getCoreSize(), // 核心线程数
						threadPoolProperties.getMaxSize(), // 最大线程数
						threadPoolProperties.getKeepAlive(), SECONDS,
						new LinkedBlockingQueue<>(threadPoolProperties.getQueueCapacity()),
						new ThreadFactoryBuilder()
								.setNameFormat("push-" + nodeId + "-%d")
								.setDaemon(true)
								.build(),
						new ThreadPoolExecutor.CallerRunsPolicy() // 队列满时回退到调用线程
				)
		);
	}

	@Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
	void cleanIdleExecutors() {
		nodeExecutors.entrySet().removeIf(e ->
				e.getValue().getPoolSize() == 0 &&
						e.getValue().getQueue().isEmpty()
		);
	}

	@PreDestroy
	public void destroy() {
		// 1. 停止接收新任务
		nodeExecutors.values().forEach(ExecutorService::shutdown);

		// 2. 关闭MQ连接
		rocketMQTemplate.destroy();

		// 3. 关闭调度器
		localPushScheduler.dispose();

		nodeExecutors.values().forEach(exec -> {
			try {
				if (!exec.awaitTermination(30, SECONDS)) {
					List<Runnable> dropped = exec.shutdownNow();
					log.warn("线程池强制关闭: 丢弃任务={}", dropped.size());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
	}
}
