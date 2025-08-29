package com.luohuo.flex.ws.websocket.nacos.listener;

import com.luohuo.basic.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.router.NacosRouterService;
import com.luohuo.flex.router.RouterCacheKeyBuilder;
import com.luohuo.flex.ws.websocket.SessionManager;
import com.luohuo.flex.ws.websocket.entity.NodeDownMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 节点下线消息监听器
 *
 * 监听节点下线消息，处理节点下线后的会话迁移。
 */
@Slf4j
@Component
public class NodeDownMessageListener extends AbstractRedisChannelMessageListener<NodeDownMessage> {

	private final SessionManager sessionManager;
	private final NacosRouterService routerService;
	RedisTemplate<String, Object> redisTemplate;

	public NodeDownMessageListener(SessionManager sessionManager, NacosRouterService routerService, RedisTemplate<String, Object> redisTemplate) {
		this.sessionManager = sessionManager;
		this.routerService = routerService;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void onMessage(NodeDownMessage message) {
		log.warn("收到节点下线通知: {}", message.getNodeId());
		migrateSessions(message.getNodeId());
	}

	private void migrateSessions(String downNodeId) {
		try {
			// 1. 获取受影响用户
			Map<Long, List<String>> deviceMap = routerService.getDevicesByNode(downNodeId);

			deviceMap.forEach((uid, clientIds) ->
				clientIds.forEach(clientId -> {
					sessionManager.syncOnline(uid, clientId, false); // 标记设备下线
					// 1. 按设备通知重连
					sessionManager.sendToDevice(uid, clientId, new WsBaseResp<>());

					// 2. 清理路由
					routerService.removeDeviceRoute(uid, clientId, downNodeId);
				})
			);

			redisTemplate.delete(RouterCacheKeyBuilder.buildNodeDevices(downNodeId).getKey());
		} catch (Exception e) {
			log.error("处理节点下线失败", e);
		}
	}
}