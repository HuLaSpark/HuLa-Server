package com.hula.common.event;

import com.hula.core.chat.domain.dto.ChatMsgSendDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author nyh
 */
@Getter
public class MessageSendEvent extends ApplicationEvent {
    private final ChatMsgSendDto chatMsgSendDto;

    public MessageSendEvent(Object source, ChatMsgSendDto chatMsgSendDto) {
        super(source);
        this.chatMsgSendDto = chatMsgSendDto;
    }
}
