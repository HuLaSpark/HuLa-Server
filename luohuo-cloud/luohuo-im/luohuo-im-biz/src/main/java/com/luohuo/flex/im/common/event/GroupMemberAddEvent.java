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
	// 群里有多少人
	private final Integer totalNum;
	// 在线有多少人
	private final Integer onlineNum;
	// 消息接收人
	private final Long uid;

    public GroupMemberAddEvent(Object source, Long roomId, Integer totalNum, Integer onlineNum, List<Long> memberList, Long uid) {
        super(source);
        this.memberList = memberList;
		this.roomId = roomId;
		this.totalNum = totalNum;
		this.onlineNum = onlineNum;
		this.uid = uid;
    }

}
