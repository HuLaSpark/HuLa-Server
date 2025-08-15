package com.luohuo.flex.ws.consumer;

import com.luohuo.flex.ws.dto.ScanSuccessMessageDTO;
import lombok.AllArgsConstructor;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 将扫码成功的信息发送给对应的用户,等待授权
 * @author nyh
 */
@AllArgsConstructor
@RocketMQMessageListener(consumerGroup = "user_scan_send_msg_group", topic = "user_scan_send_msg", messageModel = MessageModel.BROADCASTING)
@Component
public class ScanSuccessConsumer implements RocketMQListener<ScanSuccessMessageDTO> {

    @Override
    public void onMessage(ScanSuccessMessageDTO scanSuccessMessageDTO) {
    }

}
