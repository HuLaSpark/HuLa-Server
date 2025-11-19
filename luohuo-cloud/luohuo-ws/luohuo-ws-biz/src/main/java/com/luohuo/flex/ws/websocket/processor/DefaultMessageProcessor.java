package com.luohuo.flex.ws.websocket.processor;

import com.luohuo.flex.model.ws.WSBaseReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * 默认处理所有未被其他处理器处理的消息
 */
@Slf4j
@Order(100)
@Component
public class DefaultMessageProcessor implements MessageProcessor {
    @Override
    public boolean supports(WSBaseReq req) {
        return true;
    }

    @Override
    public void process(WebSocketSession session, Long uid, WSBaseReq req) {
        log.warn("未知消息类型: {}", req.getType());
    }
}