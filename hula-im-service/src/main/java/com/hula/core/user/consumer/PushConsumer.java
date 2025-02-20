package com.hula.core.user.consumer;

import com.hula.common.constant.MqConstant;
import com.hula.common.domain.dto.PushMessageDTO;
import com.hula.core.user.domain.enums.WSPushTypeEnum;
import com.hula.core.user.service.WebSocketService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;


/**
 * @author nyh
 */
@Slf4j
@RocketMQMessageListener(topic = MqConstant.PUSH_TOPIC, consumerGroup = MqConstant.PUSH_GROUP, messageModel = MessageModel.BROADCASTING)
@Component
public class PushConsumer implements RocketMQListener<PushMessageDTO> {
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void onMessage(PushMessageDTO message) {
		log.error("收到消息，推送到前端......", message);
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());
        switch (wsPushTypeEnum) {
            case WSPushTypeEnum.USER:
                message.getUidList().forEach(uid -> {
                    webSocketService.sendUser(message.getWsBaseMsg(), uid);
                });
                break;
            case WSPushTypeEnum.ALL:
                webSocketService.sendAll(message.getWsBaseMsg(), null);
                break;
        }
    }
}
