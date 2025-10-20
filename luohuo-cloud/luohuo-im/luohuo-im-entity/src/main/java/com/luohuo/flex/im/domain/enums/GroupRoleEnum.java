package com.luohuo.flex.im.domain.enums;

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
    LEADER(1, "群主"),
    MANAGER(2, "管理"),
    MEMBER(3, "普通成员"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, GroupRoleEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleEnum.values()).collect(Collectors.toMap(GroupRoleEnum::getType, Function.identity()));
    }

    public static GroupRoleEnum of(Integer type) {
        return cache.get(type);
    }

	/**
	 * 返回角色的名称
	 * @param type 传入成员类型
	 */
	public static String get(Integer type) {
		if(type > MANAGER.getType()){
			return "";
		}
		return cache.get(type).getDesc();
	}
}
