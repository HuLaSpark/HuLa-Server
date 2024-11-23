package com.hula.core.user.consumer;

import com.hula.common.constant.MQConstant;
import com.hula.common.domain.dto.PushMessageDTO;
import com.hula.core.user.domain.enums.WSPushTypeEnum;
import com.hula.core.user.service.WebSocketService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;


/**
 * @author nyh
 */
@RocketMQMessageListener(topic = MQConstant.PUSH_TOPIC, consumerGroup = MQConstant.PUSH_GROUP, messageModel = MessageModel.BROADCASTING)
@Component
public class PushConsumer implements RocketMQListener<PushMessageDTO> {
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void onMessage(PushMessageDTO message) {
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());
        switch (wsPushTypeEnum) {
            case WSPushTypeEnum.USER:
                message.getUidList().forEach(uid -> {
                    webSocketService.sendToUid(message.getWsBaseMsg(), uid);
                });
                break;
            case WSPushTypeEnum.ALL:
                webSocketService.sendToAllOnline(message.getWsBaseMsg(), message.getUid());
                break;
        }
    }
}
