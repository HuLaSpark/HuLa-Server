package com.hula.core.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 成员角色枚举
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum GroupRoleEnum {
    /**
     *
     */
    LEADER(1, "群主"),
    MANAGER(2, "管理"),
    MEMBER(3, "普通成员"),
    ;

    private final Integer type;
    private final String desc;

    public static final List<Integer> ADMIN_LIST = Arrays.asList(GroupRoleEnum.LEADER.getType(), GroupRoleEnum.MANAGER.getType());

    private static Map<Integer, GroupRoleEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleEnum.values()).collect(Collectors.toMap(GroupRoleEnum::getType, Function.identity()));
    }

    public static GroupRoleEnum of(Integer type) {
        return cache.get(type);
    }
}
