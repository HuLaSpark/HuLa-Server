package com.luohuo.flex.ws.websocket.processor;

import com.luohuo.flex.model.ws.WSBaseReq;
import com.luohuo.flex.model.enums.WSReqTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * 心跳消息处理机制
 */
@Slf4j
@Order(1)
@Component
public class HeartbeatProcessor implements MessageProcessor {
    @Override
    public boolean supports(WSBaseReq req) {
        return WSReqTypeEnum.HEARTBEAT.eq(req.getType());
    }

    @Override
    public void process(WebSocketSession session, Long uid, WSBaseReq req) {
//        log.info("收到用户 {} 的心跳", uid);
    }
}