package com.hula.ai.llm.deepseek.enmus;

import lombok.Getter;

/**
 * 模型枚举
 * @author: 云裂痕
 * @date: 2025/2/17
 * @version: 1.2.4
 * Copyright Ⓒ 2024 Master Computer Corporation Limited All rights reserved.
 */
@Getter
public enum Model {

    /**
     * deepseek-reasoner 代表R1模型
     */
    CHAT("deepseek-chat"),

    REASONER("deepseek-reasoner"),

    ;
    private final String name;

    Model(String name) {
        this.name = name;
    }

}
