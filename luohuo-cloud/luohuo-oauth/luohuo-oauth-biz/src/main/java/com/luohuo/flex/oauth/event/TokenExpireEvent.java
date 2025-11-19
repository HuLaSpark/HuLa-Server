package com.luohuo.flex.oauth.event;

import com.luohuo.flex.model.entity.ws.OffLineResp;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * token过期事件
 * @author ZOL
 */
@Getter
public class TokenExpireEvent extends ApplicationEvent {

    private final OffLineResp offLine;

    public TokenExpireEvent(Object source, OffLineResp offLine) {
        super(source);
        this.offLine = offLine;
    }
}
