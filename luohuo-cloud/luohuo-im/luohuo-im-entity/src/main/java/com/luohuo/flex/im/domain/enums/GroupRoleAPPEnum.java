package com.luohuo.flex.im.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 热点枚举
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum GroupRoleAPPEnum {
    LEADER(1, "群主"),
    MANAGER(2, "管理"),
    MEMBER(3, "普通成员"),
    REMOVE(4, "被移除的成员"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, GroupRoleAPPEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleAPPEnum.values()).collect(Collectors.toMap(GroupRoleAPPEnum::getType, Function.identity()));
    }

    public static GroupRoleAPPEnum of(Integer type) {
        return cache.get(type);
    }
}
