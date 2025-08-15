package com.luohuo.flex.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通话状态
 */
public enum CallResponseStatus {
    TIMEOUT(-1, "超时未接听"),
    REJECTED(0, "已拒绝"),
    ACCEPTED(1, "已接听"),
    HANGUP(2, "已挂断");

    private final int code;
    private final String desc;

    CallResponseStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

	private static Map<Integer, CallResponseStatus> cache;

	static {
		cache = Arrays.stream(CallResponseStatus.values()).collect(Collectors.toMap(CallResponseStatus::getCode, Function.identity()));
	}

	public static CallResponseStatus of(Integer type) {
		return cache.get(type);
	}
}