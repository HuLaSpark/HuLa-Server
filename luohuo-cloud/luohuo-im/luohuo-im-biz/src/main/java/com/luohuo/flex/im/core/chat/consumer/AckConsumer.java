package com.luohuo.flex.im.core.chat.consumer;

import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.flex.common.cache.PassageMsgCacheKeyBuilder;
import com.luohuo.flex.common.constant.MqConstant;
import com.luohuo.flex.im.core.chat.dao.ContactDao;
import com.luohuo.flex.im.core.chat.service.cache.MsgCache;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.model.ws.AckMessageDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 目前架构ws服务无法处理业务，客户端回执给ws服务之后进行mq跳转至此
 * @author 乾乾
 */
@Slf4j
@RocketMQMessageListener(consumerGroup = MqConstant.MSG_PUSH_ACK_TOPIC_GROUP, topic = MqConstant.MSG_PUSH_ACK_TOPIC, messageModel = MessageModel.CLUSTERING)
@Component
@AllArgsConstructor
public class AckConsumer implements RocketMQListener<AckMessageDTO> {

    private MsgCache msgCache;
    private ContactDao contactDao;
	private CachePlusOps cachePlusOps;

	/**
	 * 通过mq的方式 回调进行回执
	 * @param dto
	 */
    @Override
    public void onMessage(AckMessageDTO dto) {
		Message message = msgCache.get(dto.getMsgId());
		if(message == null){
			return;
		}

		// 1. 更新收到消息的状态
		contactDao.refreshOrCreateActiveTime(message.getRoomId(), Arrays.asList(dto.getUid()), message.getId(), message.getCreateTime());

		// 2. 删除在途消息
		cachePlusOps.sRem(PassageMsgCacheKeyBuilder.build(dto.getUid()), message.getId());
    }
}
