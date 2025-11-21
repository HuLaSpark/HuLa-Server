package com.luohuo.flex.msg.event;

import org.springframework.context.ApplicationEvent;

/**
 * 消息发送事件
 *
 * @author 乾乾
 * @date 2020年03月18日17:22:55
 */
public class MsgSendEvent extends ApplicationEvent {
    public MsgSendEvent(MsgEventVO msg) {
        super(msg);
    }
}
