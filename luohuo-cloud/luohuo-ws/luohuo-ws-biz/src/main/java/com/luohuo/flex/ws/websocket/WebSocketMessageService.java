package com.luohuo.flex.ws.websocket;

import com.luohuo.flex.ws.websocket.processor.MessageHandlerChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * 独立消息处理器
 */
@Slf4j
@Service
public class WebSocketMessageService {
	private final MessageHandlerChain handlerChain;

	public WebSocketMessageService(MessageHandlerChain handlerChain) {
		this.handlerChain = handlerChain;
	}

	/**
	 * 消息处理
	 * @param session 当前会话
	 * @param uid 当前uid
	 * @param message 消息实体
	 */
	public void handleMessage(WebSocketSession session, Long uid, WebSocketMessage message) {
		handlerChain.handleMessage(session, uid, message.getPayloadAsText());
	}
}