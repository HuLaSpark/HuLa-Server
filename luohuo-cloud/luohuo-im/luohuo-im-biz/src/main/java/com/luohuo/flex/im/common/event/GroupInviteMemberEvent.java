package com.luohuo.flex.im.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author 邀请进群事件
 */
@Getter
public class GroupInviteMemberEvent extends ApplicationEvent {

    // 变动的成员
    private final List<Long> memberList;
    private final Long roomId;
    // 消息接收人
    private final Long uid;
    // 是否是申请进群
    private final Boolean applyFor;
    private final Integer channel;

    public GroupInviteMemberEvent(Object source, Long roomId, List<Long> memberList, Long uid, Boolean applyFor, Integer channel) {
        super(source);
        this.memberList = memberList;
        this.roomId = roomId;
        this.uid = uid;
        this.applyFor = applyFor;
        this.channel = channel;
    }

}
