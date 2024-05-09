package com.hula.core.user.consumer;

import com.hula.common.constant.MQConstant;
import com.hula.common.domain.dto.ScanSuccessMessageDTO;
import com.hula.core.user.service.WebSocketService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 将扫码成功的信息发送给对应的用户,等待授权
 * @author nyh
 */
@RocketMQMessageListener(consumerGroup = MQConstant.SCAN_MSG_GROUP, topic = MQConstant.SCAN_MSG_TOPIC, messageModel = MessageModel.BROADCASTING)
@Component
public class ScanSuccessConsumer implements RocketMQListener<ScanSuccessMessageDTO> {
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void onMessage(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        webSocketService.scanSuccess(scanSuccessMessageDTO.getCode());
    }

}
