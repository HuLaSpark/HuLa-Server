package com.luohuo.flex.ws.consumer;

import com.luohuo.flex.ws.dto.LoginMessageDTO;
import lombok.AllArgsConstructor;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 在本地服务上找寻对应channel，将对应用户登陆，并触发所有用户收到上线事件
 * @author nyh
 */
@AllArgsConstructor
@RocketMQMessageListener(consumerGroup = "user_login_send_msg_group", topic = "user_login_send_msg", messageModel = MessageModel.BROADCASTING)
@Component
public class MsgLoginConsumer implements RocketMQListener<LoginMessageDTO> {

    @Override
    public void onMessage(LoginMessageDTO loginMessageDTO) {
        // 尝试登录
    }

}
