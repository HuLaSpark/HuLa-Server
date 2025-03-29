package com.hula.ai.client.enums;

import lombok.Getter;

/**
 * 聊天角色枚举类
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Getter
public enum ChatRoleEnum {

    /**
     * 角色
     */
    SYSTEM("system", "系统"),

    ASSISTANT("assistant", "角色"),

    USER("user", "用户");

    /**
     * 值
     */
    private final String value;

    /**
     * 标签
     */
    private final String label;

    ChatRoleEnum(final String value, final String label) {
        this.label = label;
        this.value = value;
    }

}
