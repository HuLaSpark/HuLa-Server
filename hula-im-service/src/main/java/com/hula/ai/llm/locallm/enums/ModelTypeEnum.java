package com.hula.ai.llm.locallm.enums;

import lombok.Getter;

/**
 * Langchain 对话模型类型
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Getter
public enum ModelTypeEnum {

    /**
     * 对话模型类型
     */
    LANGCHAIN("LANGCHAIN", 1),
    OLLAMA("Ollama", 2),
    GITEE_AI("GiteeAI", 3),
    COZE("Coze", 4),
    FASTGPT("FastGPT", 5),

    ;

    /**
     * 值
     */
    private final String label;

    /**
     * 标签
     */
    private final Integer value;

    ModelTypeEnum(final String label, final Integer value) {
        this.label = label;
        this.value = value;
    }

    public static ModelTypeEnum getEnum(Integer value) {
        for (ModelTypeEnum item : ModelTypeEnum.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }

}
