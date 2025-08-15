package com.luohuo.flex.oauth.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;


/**
 * 幂等类型
 * @author 乾乾
 */
@AllArgsConstructor
@Getter
public enum LoginTypeEnum {
    PC("PC", "PC端"),
    MOBILE("MOBILE", "移动端"),
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
