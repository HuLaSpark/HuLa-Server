package com.hula.ai.llm.openai.entity.chat.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolChoiceObj {
    /**
     * 需要调用的方法名称
     */
    private ToolChoiceObjFunction function;
    /**
     * 工具的类型。目前仅支持函数。
     *
     * @see Type
     */
    private String type;

    @Getter
    @AllArgsConstructor
    public enum Type {
        FUNCTION("function"),
        ;
        private final String name;
    }
}
