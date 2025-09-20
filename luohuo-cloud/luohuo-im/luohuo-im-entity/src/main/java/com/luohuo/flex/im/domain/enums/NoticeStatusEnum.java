package com.luohuo.flex.im.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 事件处理的枚举
 *
 * @author 乾乾
 */
@RequiredArgsConstructor
@Getter
public enum NoticeStatusEnum implements Serializable {
	UNTREATED(0, "待审批"),
	ACCEPTED(1, "已同意"),
	REJECTED(2, "已拒绝"),
	IGNORE(3, "已忽略");

	private final Integer status;
	private final String desc;

	/**
	 * 根据当前枚举的name匹配
	 */
	public static NoticeStatusEnum match(Integer val) {
		return Stream.of(values()).parallel().filter(item -> item.getStatus().equals(val)).findAny().orElse(ACCEPTED);
	}

	public static NoticeStatusEnum get(Integer val) {
		return match(val);
	}
}
