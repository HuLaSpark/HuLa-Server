package com.hula.ai.llm.locallm.coze.enums;

import lombok.Getter;

/**
 * 消息内容类型
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Getter
public enum ContentTypeEnum {

    /**
     * 消息内容类型
     */
    TEXT("text", "文本"),
    OBJECT_STRING("object_string", "多模态内容"),
    CARD("card", "卡片"),

    ;

    /**
     * 值
     */
    private final String value;

    /**
     * 标签
     */
    private final String label;

    ContentTypeEnum(final String value, final String label) {
        this.value = value;
        this.label = label;
    }

}
