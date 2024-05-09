package com.hula.common.event;

import com.hula.core.user.domain.entity.UserBackpack;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author nyh
 */
@Getter
public class ItemReceiveEvent extends ApplicationEvent {
    private final UserBackpack userBackpack;

    public ItemReceiveEvent(Object source, UserBackpack userBackpack) {
        super(source);
        this.userBackpack = userBackpack;
    }

}
