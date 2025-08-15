package com.luohuo.flex.im.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 是否正常的通用枚举
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum NormalOrNoEnum {
    NORMAL(0, "正常"),
    NOT_NORMAL(1, "不正常"),
    ;

    private final Integer status;
    private final String desc;

    private static Map<Integer, NormalOrNoEnum> cache;

    static {
        cache = Arrays.stream(NormalOrNoEnum.values()).collect(Collectors.toMap(NormalOrNoEnum::getStatus, Function.identity()));
    }

    public static NormalOrNoEnum of(Integer type) {
        return cache.get(type);
    }

    public static Integer toStatus(Boolean bool) {
        return bool ? NORMAL.getStatus() : NOT_NORMAL.getStatus();
    }
}
