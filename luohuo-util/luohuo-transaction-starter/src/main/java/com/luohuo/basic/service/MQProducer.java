package com.luohuo.basic.service;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import com.luohuo.basic.annotation.SecureInvoke;

/**
 * Description: 发送mq工具类
 * Date: 2023-08-12
 */
@Component
public class MQProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

	public void sendMsg(String topic, Object body) {
		Message<Object> build = MessageBuilder.withPayload(body).build();
		rocketMQTemplate.send(topic, build);
	}

    /**
     * 发送可靠消息，在事务提交后保证发送成功
     *
     * @param topic
     * @param body
     */
    @SecureInvoke
    public void sendSecureMsg(String topic, Object body, Object key) {
        Message<Object> build = MessageBuilder
                .withPayload(body)
                .setHeader("KEYS", key)
                .build();
        rocketMQTemplate.send(topic, build);
    }
}
