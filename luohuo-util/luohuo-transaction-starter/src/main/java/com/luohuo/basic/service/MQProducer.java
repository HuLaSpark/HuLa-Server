package com.luohuo.basic.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import com.luohuo.basic.annotation.SecureInvoke;

/**
 * Description: 发送mq工具类
 * Date: 2023-08-12
 */
@Slf4j
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

	/**
	 * 发送可靠延迟消息
	 *
	 * @param topic 主题
	 * @param body 消息体
	 * @param delayLevel 延迟级别
	 */
	public void sendMsgWithDelay(String topic, Object body, int delayLevel) {
		rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(body).build(), calculateTimeoutEnhanced(delayLevel), calculateDelayLevel(delayLevel));
	}

	// RocketMQ支持的延迟级别映射 对应配置文件里面的!!!
	private static final int[] ROCKETMQ_DELAY_LEVELS = {
			1,    // 1s
			3,    // 3s
			5,    // 5s
			10,   // 10s
			30,   // 30s
			60,   // 1m
			120,  // 2m
			180,  // 3m
			240,  // 4m
			300,  // 5m
			360,  // 6m
			420,  // 7m
			480,  // 8m
			540,  // 9m
			600,  // 10m
			1200, // 20m
			1800, // 30m
			3600, // 1h
			7200  // 2h
	};

	/**
	 * 计算最接近的RocketMQ延迟级别
	 *
	 * @param delaySeconds 延迟秒数
	 * @return RocketMQ延迟级别 (1-19)
	 */
	private int calculateDelayLevel(long delaySeconds) {
		// 如果延迟时间小于1秒，按1秒处理
		if (delaySeconds <= 0) {
			return 1;
		}

		// 查找最接近的延迟级别
		for (int i = 0; i < ROCKETMQ_DELAY_LEVELS.length; i++) {
			if (delaySeconds <= ROCKETMQ_DELAY_LEVELS[i]) {
				return i + 1;
			}
		}

		// 超过最大延迟时间，使用最大级别
		return ROCKETMQ_DELAY_LEVELS.length;
	}

	/**
	 * 动态计算延迟消息发送的超时时间
	 * @param delayLevel
	 * @return
	 */
	private long calculateTimeoutEnhanced(int delayLevel) {
		if (delayLevel < 1 || delayLevel > ROCKETMQ_DELAY_LEVELS.length) {
			delayLevel = ROCKETMQ_DELAY_LEVELS.length;
		}

		long delaySeconds = ROCKETMQ_DELAY_LEVELS[delayLevel - 1];
		long delayMillis = delaySeconds * 1000L;

		// 分段超时策略
		if (delaySeconds <= 30) {
			// 短延迟： + 5秒缓冲
			return delayMillis + 5000L;
		} else if (delaySeconds <= 300) {
			// 中等延迟： + 10秒缓冲
			return delayMillis + 10000L;
		} else {
			// 长延迟：+ 30秒缓冲，但最大不超过1小时
			long maxTimeout = 60 * 60 * 1000L;
			return Math.min(delayMillis + 30000L, maxTimeout);
		}
	}
}
