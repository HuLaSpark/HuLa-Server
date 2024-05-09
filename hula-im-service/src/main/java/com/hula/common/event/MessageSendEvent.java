package com.hula.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author nyh
 */
@Getter
public class MessageSendEvent extends ApplicationEvent {
    private final Long msgId;

    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
