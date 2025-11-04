package com.luohuo.flex.ws.websocket.processor;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.common.constant.DefValConstants;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.model.enums.WSReqTypeEnum;
import com.luohuo.flex.model.ws.AckMessageDTO;
import com.luohuo.flex.model.ws.WSBaseReq;
import com.luohuo.flex.ws.ReactiveContextUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * 客户端确认收到消息
 */
@Slf4j
@Order(13)
@Component
@RequiredArgsConstructor
public class AckProcessor implements MessageProcessor {
	@Resource
	private RocketMQTemplate rocketMQTemplate;

    @Override
    public boolean supports(WSBaseReq req) {
        return WSReqTypeEnum.ACK.eq(req.getType());
    }

    @Override
    public void process(WebSocketSession session, Long uid, WSBaseReq payload) {
		if(ReactiveContextUtil.getTenantId() == null){
			ReactiveContextUtil.setTenantId(DefValConstants.DEF_TENANT_ID);
			ReactiveContextUtil.setUid(uid);
		}

		AckMessageDTO req = JSONUtil.toBean(payload.getData(), AckMessageDTO.class);
		req.setUid(uid);
		rocketMQTemplate.send(MqConstant.MSG_PUSH_ACK_TOPIC, MessageBuilder.withPayload(req).build());
	}
}