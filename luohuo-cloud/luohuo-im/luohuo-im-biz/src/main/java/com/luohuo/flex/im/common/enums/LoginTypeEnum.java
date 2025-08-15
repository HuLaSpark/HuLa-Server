package com.luohuo.flex.im.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;


/**
 * 幂等类型 TODO 需要删除
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum LoginTypeEnum {
    PC("pc", "PC端"),
    MOBILE("mobile", "移动端"),
    ;

    private final String type;
    private final String desc;

	public static LoginTypeEnum match(String val, LoginTypeEnum def) {
		return Stream.of(values()).parallel().filter((item) -> item.getType().equals(val)).findAny().orElse(def);
	}

	public static LoginTypeEnum get(String val) {
		return match(val, null);
	}
}
