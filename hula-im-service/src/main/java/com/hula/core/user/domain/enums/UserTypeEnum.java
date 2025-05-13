package com.hula.core.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum UserTypeEnum {
    SYSTEM(1, "系统用户"),
    BOT(2, "机器人"),
    NORMAL(3, "普通用户");

    private final Integer value;
    private final String desc;
    private final static Map<Integer, UserTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(UserTypeEnum.values()).collect(Collectors.toMap(UserTypeEnum::getValue, Function.identity()));
    }

    public static UserTypeEnum of(Integer value) {
        return CACHE.get(value);
    }
}
