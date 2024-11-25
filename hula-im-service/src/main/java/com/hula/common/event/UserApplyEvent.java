package com.hula.common.event;

import com.hula.core.user.domain.entity.UserApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author nyh
 */
@Getter
public class UserApplyEvent extends ApplicationEvent {
    private final UserApply userApply;

    public UserApplyEvent(Object source, UserApply userApply) {
        super(source);
        this.userApply = userApply;
    }

}
