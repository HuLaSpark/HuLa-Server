package com.luohuo.flex.im.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import com.luohuo.flex.im.domain.entity.UserApply;

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
