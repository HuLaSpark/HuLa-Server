package com.luohuo.flex.common.constant;

/**
 * @author nyh
 */
public interface MqConstant {
	/**
	 * 内部消息发送的事件
	 */
	String FRONTEND_MSG_INPUT_TOPIC = "frontend_msg_input_topic";
	String FRONTEND_MSG_INPUT_TOPIC_GROUP = "frontend_msg_input_topic_group";

    /**
     * 前端消息发送到im服务, im异步推送到MQ的事件，MQ再具体路由到对应的ws服务，最后推送到前端
     */
    String MSG_PUSH_OUTPUT_TOPIC = "msg_push_output_topic";
    String MSG_PUSH_OUTPUT_TOPIC_GROUP = "msg_push_output_topic_group";

    /**
     * push用户
     */
    String PUSH_TOPIC = "websocket_push";
    String PUSH_GROUP = "websocket_push_group";

    /**
     * (授权完成后)登录信息mq
     */
    String LOGIN_MSG_TOPIC = "user_login_send_msg";
    String LOGIN_MSG_GROUP = "user_login_send_msg_group";

    /**
     * 扫码成功 信息发送mq
     */
    String SCAN_MSG_TOPIC = "user_scan_send_msg";
    String SCAN_MSG_GROUP = "user_scan_send_msg_group";
}
