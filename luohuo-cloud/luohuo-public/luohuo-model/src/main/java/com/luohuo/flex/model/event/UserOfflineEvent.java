package com.luohuo.flex.model.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * @author nyh
 */
@Getter
public class UserOfflineEvent extends ApplicationEvent {
	private final Long uid;
	private final Long defUserId;
	private final Long tenantId;
	private final LocalDateTime lastOptTime;

    public UserOfflineEvent(Object source, Long tenantId, Long defUserId, Long uid, LocalDateTime lastOptTime) {
        super(source);
        this.uid = uid;
		this.defUserId = defUserId;
		this.tenantId = tenantId;
		this.lastOptTime = lastOptTime;
    }
}
