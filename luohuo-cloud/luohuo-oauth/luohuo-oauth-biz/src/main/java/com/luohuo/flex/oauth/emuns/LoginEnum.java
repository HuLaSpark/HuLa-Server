package com.luohuo.flex.oauth.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 登录系统，还有一种是系统超级管理员
 *
 * @author 乾乾
 * @date 2025/06/09 11:26 上午
 */
@Getter
@AllArgsConstructor
public enum LoginEnum {
	MANAGER(1, "管理端登录: admin|RAM"),
	IM(2, "IM系统登录"),
    ;
    private final Integer val;
    private final String describe;

	public static LoginEnum match(Integer val, LoginEnum def) {
		return Stream.of(values()).parallel().filter((item) -> item.getVal().equals(val)).findAny().orElse(def);
	}

	public static LoginEnum get(Integer val) {
		return match(val, null);
	}
}
