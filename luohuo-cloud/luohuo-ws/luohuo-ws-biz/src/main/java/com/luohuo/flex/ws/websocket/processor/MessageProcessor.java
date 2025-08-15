package com.luohuo.flex.ws.websocket.processor;

import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * 消息处理器标准接口
 */
public interface MessageProcessor {
	/**
	 * 轻量级验证，避免复杂逻辑
	 *
	 * @param payload 消息内容
	 */
	boolean supports(String payload);

	/**
	 * 业务处理需捕获自身异常
	 * @param session 会话
	 * @param uid 用户id
	 * @param payload 消息内容
	 */
	void process(WebSocketSession session, Long uid, String payload);
}