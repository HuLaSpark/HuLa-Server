package com.luohuo.flex.im.core.chat.consumer;

import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.flex.common.cache.PassageMsgCacheKeyBuilder;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.core.chat.dao.ContactDao;
import com.luohuo.flex.im.core.user.service.impl.PushService;
import com.luohuo.flex.model.entity.dto.NodePushDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 消息推送延迟二次推送专属消费者 [系统自己发起]
 * 收到重试消息之后判断路由的uid是否传递的消息是否还在途中，再途的话再次发送
 * @author 乾乾
 */
@Slf4j
@RocketMQMessageListener(
		topic = MqConstant.PUSH_DELAY_TOPIC,
		consumerGroup = MqConstant.PUSH_DELAY_GROUP,
		messageModel = MessageModel.CLUSTERING,
		maxReconsumeTimes = 3
)
@Component
@RequiredArgsConstructor
public class RetryPushConsumer implements RocketMQListener<NodePushDTO> {
	private final CachePlusOps cachePlusOps;
	private final PushService pushService;
	private final ContactDao contactDao;

    @Override
	public void onMessage(NodePushDTO message) {
		Map<String, Long> deviceUserMap = message.getDeviceUserMap();
		deviceUserMap.values().forEach(uid -> {
			Boolean exist = cachePlusOps.sIsMember(PassageMsgCacheKeyBuilder.build(uid), message.getHashId());

			if (exist) {
				log.info("ack失败重新发送消息: {}", message);
				pushService.sendPushMsg(message.getWsBaseMsg(), Arrays.asList(uid), message.getUid());

				// 直接更新会话的最后一条消息的id
				LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) message.getWsBaseMsg().getData();
				LinkedHashMap<String, Object> msg =  (LinkedHashMap<String, Object>) dataMap.get("message");
				LinkedHashMap<String, Object> user =  (LinkedHashMap<String, Object>) dataMap.get("fromUser");
				contactDao.refreshOrCreateActive(msg.get("roomId"), Arrays.asList(Long.parseLong(user.get("uid").toString())), msg.get("id"), msg.get("sendTime"));
			}
		});
	}
}
