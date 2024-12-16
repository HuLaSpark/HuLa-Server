package com.hula.core.user.consumer;

import com.hula.common.constant.MqConstant;
import com.hula.common.domain.dto.LoginMessageDTO;
import com.hula.core.user.service.WebSocketService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 在本地服务上找寻对应channel，将对应用户登陆，并触发所有用户收到上线事件
 * @author nyh
 */
@RocketMQMessageListener(consumerGroup = MqConstant.LOGIN_MSG_GROUP, topic = MqConstant.LOGIN_MSG_TOPIC, messageModel = MessageModel.BROADCASTING)
@Component
public class MsgLoginConsumer implements RocketMQListener<LoginMessageDTO> {
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void onMessage(LoginMessageDTO loginMessageDTO) {
        // 尝试登录
        webSocketService.scanLoginSuccess(loginMessageDTO);
    }

}
