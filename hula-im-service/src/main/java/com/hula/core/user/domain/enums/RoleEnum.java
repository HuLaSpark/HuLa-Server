package com.hula.core.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 角色枚举
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN(1L, "超级管理员"),
    CHAT_MANAGER(2L, "HuLa群聊管理"),
    ;

    private final Long id;
    private final String desc;

    private static Map<Long, RoleEnum> cache;

    static {
        cache = Arrays.stream(RoleEnum.values()).collect(Collectors.toMap(RoleEnum::getId, Function.identity()));
    }

    public static RoleEnum of(Long type) {
        return cache.get(type);
    }
}
