package com.hula.common.event;

import com.hula.core.user.domain.vo.resp.user.OffLineResp;
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
