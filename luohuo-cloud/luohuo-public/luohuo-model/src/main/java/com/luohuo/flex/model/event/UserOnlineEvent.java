package com.luohuo.flex.model.event;

import com.luohuo.flex.model.entity.base.IpInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * @author 乾乾
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {

	private final Long uid;
	private final Long userId;
	private final Long tenantId;
	private final LocalDateTime lastOptTime;
	private final IpInfo ipInfo;

    public UserOnlineEvent(Object source, Long tenantId, Long uid, Long userId, LocalDateTime lastOptTime, IpInfo ipInfo) {
        super(source);
		this.uid = uid;
		this.userId = userId;
		this.tenantId = tenantId;
        this.lastOptTime = lastOptTime;
		this.ipInfo = ipInfo;
    }
}
