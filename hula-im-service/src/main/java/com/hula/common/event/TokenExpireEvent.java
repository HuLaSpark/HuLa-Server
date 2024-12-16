package com.hula.common.event;

import com.hula.core.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * token过期事件
 * @author ZOL
 */
@Getter
public class TokenExpireEvent extends ApplicationEvent {

    private final User user;

    public TokenExpireEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
