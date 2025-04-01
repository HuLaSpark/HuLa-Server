package com.hula.ai.llm.openai.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述： api地址
 */
@Getter
@AllArgsConstructor
public enum ChatGPTUrl {

    COMPLETIONS("https://api.openai.com/v1/completions"),
    ;

    private final String url;

}
