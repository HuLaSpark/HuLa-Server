package com.luohuo.flex.im.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 转发类型的枚举
 *
 * @author 乾乾
 */
@RequiredArgsConstructor
@Getter
public enum MergeTypeEnum implements Serializable {

	SINGLE(1, "单一转发"),
	MERGE(2, "合并转发");

	private final Integer type;
	private final String desc;

	/**
	 * 根据当前枚举的name匹配
	 */
	public static MergeTypeEnum match(Integer val) {
		return Stream.of(values()).parallel().filter(item -> item.getType().equals(val)).findAny().orElse(SINGLE);
	}

	public static MergeTypeEnum get(Integer val) {
		return match(val);
	}
}
