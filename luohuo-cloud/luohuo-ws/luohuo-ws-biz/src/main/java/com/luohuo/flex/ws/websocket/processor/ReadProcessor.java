package com.luohuo.flex.ws.websocket.processor;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.model.enums.WSReqTypeEnum;
import com.luohuo.flex.model.ws.ReadMessageDTO;
import com.luohuo.flex.model.ws.WSBaseReq;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * 用户已读的一堆消息
 */
@Slf4j
@Order(15)
@Component
@RequiredArgsConstructor
public class ReadProcessor implements MessageProcessor {
	@Resource
	private RocketMQTemplate rocketMQTemplate;

    @Override
    public boolean supports(WSBaseReq req) {
        return WSReqTypeEnum.READ.eq(req.getType());
    }

    @Override
    public void process(WebSocketSession session, Long uid, WSBaseReq payload) {
		ReadMessageDTO req = JSONUtil.toBean(payload.getData(), ReadMessageDTO.class);
		req.setUid(uid);
		rocketMQTemplate.send(MqConstant.MSG_PUSH_READ_TOPIC, MessageBuilder.withPayload(req).build());
    }
}