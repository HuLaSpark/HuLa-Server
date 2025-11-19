package com.luohuo.flex.im.core.chat.consumer;

import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.core.chat.service.ChatService;
import com.luohuo.flex.im.core.chat.service.adapter.MessageAdapter;
import com.luohuo.flex.model.ws.CallEndReq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 处理ws服务发过来的音视频消息
 * @author 乾乾
 */
@Slf4j
@RocketMQMessageListener(consumerGroup = MqConstant.FRONTEND_MSG_INPUT_TOPIC_GROUP, topic = MqConstant.FRONTEND_MSG_INPUT_TOPIC, messageModel = MessageModel.CLUSTERING)
@Component
@AllArgsConstructor
public class FrontendMsgConsumer implements RocketMQListener<CallEndReq> {

    private ChatService chatService;

	/**
	 * 解析音视频消息并发送
	 * @param callEndReq
	 */
    @Override
    public void onMessage(CallEndReq callEndReq) {
		chatService.sendMsg(MessageAdapter.buildMediumMsg(callEndReq), callEndReq.getCreator());
    }
}
