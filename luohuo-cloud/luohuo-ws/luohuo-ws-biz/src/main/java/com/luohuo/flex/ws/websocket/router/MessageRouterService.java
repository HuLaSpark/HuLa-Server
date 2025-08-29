package com.luohuo.flex.ws.websocket.router;

import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.model.entity.dto.RouterPushDTO;
import com.luohuo.flex.ws.service.PushService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息中转工具
 * 功能：其他没有依赖luohuo-router的服务，比如oauth服务; 需要将消息推送给任何一个用户就需要先将消息推送到当前消费者，再由当前消费者将消息推送到目标 uidList 所在的ws节点
 * 直接走 PushService 的服务：直接转发到 websocket_push 子节点，从而跳过当前消息信箱，节省一次网络io + 序列化
 * MessageRouterService 目前有TokenExpireListener事件、私聊群聊音视频在用; TokenExpireListener在 oauth服务，oauth无法知道目标在那个节点，因此需要经过当前路由工具
 * 动态路由: 使用 Redis 存储 com.luohuo.flex.router.RouterCacheKeyBuilder.DeviceNodeMapping({uid}:{clientId}) 映射关系, NacosRouterService.findNodeDeviceUser() 批量查询用户所在节点
 * 高效分发: 从本质上避免广播风暴，减少网络开销
 * 节点隔离: 每个节点只处理自己的 websocket_push{nodeId} 消息, 推送时只处理本节点连接的用户
 */
@Component
@RocketMQMessageListener(topic = MqConstant.PUSH_TOPIC, consumerGroup = MqConstant.PUSH_GROUP, messageModel = MessageModel.CLUSTERING)
public class MessageRouterService implements RocketMQListener<RouterPushDTO> {
	@Resource
	private PushService pushService;

	@Override
	public void onMessage(RouterPushDTO message) {
		// 1. 获取推送的成员
		List<Long> memberUids = message.getUidList();
		if (memberUids == null || memberUids.isEmpty()) return;

		// 2. 推送消息
		pushService.sendPushMsg(message.getWsBaseMsg(), message.getUidList(), message.getUid());
	}
}