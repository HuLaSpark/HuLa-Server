package com.luohuo.flex.im.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 朋友圈权限的枚举
 *
 * @author 乾乾
 */
@RequiredArgsConstructor
@Getter
public enum FeedPermissionEnum implements Serializable {

	privacy("privacy", "私密"),
	open("open", "公开"),
	notAnyone("notAnyone", "不给谁看"),
	partVisible("partVisible", "部分可见");

	/**
	 * 根据当前枚举的name匹配
	 */
	public static FeedPermissionEnum match(String val) {
		return Stream.of(values()).parallel().filter(item -> item.getType().equals(val)).findAny().orElse(open);
	}

	public static FeedPermissionEnum get(String val) {
		return match(val);
	}

    /**
     * 朋友圈类型
     */
    private final String type;
    private final String name;
}
