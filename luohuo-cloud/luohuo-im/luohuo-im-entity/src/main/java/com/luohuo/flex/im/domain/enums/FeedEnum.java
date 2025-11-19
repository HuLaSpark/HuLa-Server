package com.luohuo.flex.im.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 朋友圈的枚举
 *
 * @author 乾乾
 */
@RequiredArgsConstructor
@Getter
public enum FeedEnum implements Serializable {

    WORD(0, "纯文字"),
	IMAGE(1, "图片"),
	VIDEO(2, "视频");

	/**
	 * 根据当前枚举的name匹配
	 */
	public static FeedEnum match(Integer val) {
		return Stream.of(values()).parallel().filter(item -> item.getType().equals(val)).findAny().orElse(WORD);
	}

	public static FeedEnum get(Integer val) {
		return match(val);
	}

    /**
     * 朋友圈类型
     */
    private final Integer type;
    private final String name;
}
