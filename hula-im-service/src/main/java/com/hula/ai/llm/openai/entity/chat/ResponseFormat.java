package com.hula.ai.llm.openai.entity.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 指定模型必须输出的格式的对象。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFormat {
    /**
     * 默认：text
     *
     * @see Type
     */
    private String type;

    @Getter
    @AllArgsConstructor
    public enum Type {
        JSON_OBJECT("json_object"),
        TEXT("text"),
        ;
        private final String name;
    }
}
