package com.hula.core.user.service.impl;

import com.hula.common.constant.MqConstant;
import com.hula.common.domain.dto.PushMessageDTO;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.service.MQProducer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nyh
 */
@Service
public class PushService {
    @Resource
    private MQProducer mqProducer;

	/**
	 * 单个用户推送
	 */
    public void sendPushMsg(WsBaseResp<?> msg, List<Long> uidList, Long uid) {
        mqProducer.sendMsg(MqConstant.PUSH_TOPIC, new PushMessageDTO(uidList, msg, uid));
    }

	/**
	 * 单个用户推送
	 */
    public void sendPushMsg(WsBaseResp<?> msg, Long uid, Long cuid) {
        mqProducer.sendMsg(MqConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg, cuid));
    }

	/**
	 * 全员推送
	 */
    public void sendPushMsg(WsBaseResp<?> msg, Long uid) {
        mqProducer.sendMsg(MqConstant.PUSH_TOPIC, new PushMessageDTO(msg, uid));
    }
}
