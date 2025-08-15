package com.luohuo.flex.im.common.event;

import com.luohuo.flex.im.domain.dto.ChatMsgSendDto;
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
