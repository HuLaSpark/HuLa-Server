package com.luohuo.flex.im.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 事件通知的枚举
 *
 * @author 乾乾
 */
@RequiredArgsConstructor
@Getter
public enum NoticeTypeEnum implements Serializable {

	FRIEND_APPLY(1, "好友申请"),
	ADD_ME(6, "好友被申请"),
	GROUP_APPLY(2, "加群申请"),
	GROUP_INVITE(3, "群邀请"),
	GROUP_MEMBER_DELETE(5, "移除群成员"),
	GROUP_INVITE_ME(7, "被邀请进群"),
	GROUP_SET_ADMIN(8, "设置群管理员"),
	GROUP_RECALL_ADMIN(9, "取消群管理员");

	private final Integer type;
	private final String desc;

	/**
	 * 根据当前枚举的name匹配
	 */
	public static NoticeTypeEnum match(Integer val) {
		return Stream.of(values()).parallel().filter(item -> item.getType().equals(val)).findAny().orElse(FRIEND_APPLY);
	}

	public static NoticeTypeEnum get(Integer val) {
		return match(val);
	}
}
