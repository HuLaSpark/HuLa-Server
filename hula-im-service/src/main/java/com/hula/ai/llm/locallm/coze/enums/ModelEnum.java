package com.hula.ai.llm.locallm.coze.enums;

import lombok.Getter;

/**
 * 扣子 对话模型类型
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Getter
public enum ModelEnum {

    /**
     * 对话模型类型
     */
    LLM("llm模型对话", "/chat"),

    ;

    /**
     * 值
     */
    private final String value;

    /**
     * 标签
     */
    private final String url;

    ModelEnum(final String value, final String url) {
        this.value = value;
        this.url = url;
    }

}
