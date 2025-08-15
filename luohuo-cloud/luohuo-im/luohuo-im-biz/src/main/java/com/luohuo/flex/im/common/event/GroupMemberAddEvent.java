package com.luohuo.flex.im.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.List;

/**
 * @author nyh
 */
@Getter
public class GroupMemberAddEvent extends ApplicationEvent {

	// 变动的成员
    private final List<Long> memberList;
    private final Long roomId;
	private final Long cUid;

    public GroupMemberAddEvent(Object source, Long roomId, List<Long> memberList, Long cUid) {
        super(source);
        this.memberList = memberList;
        this.roomId = roomId;
		this.cUid = cUid;
    }

}
