package com.hula.ai.llm.openai.entity.assistant.run;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Data
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolCall implements Serializable {
    /**
     * The ID of the tool call.
     */
    private String id;
    /**
     * The type of tool call.
     *
     * @see Type
     */
    private String type;

    /**
     * 代码解释器工具调用定义。
     */
    @JsonProperty("code_interpreter")
    private CodeInterpreter codeInterpreter;

    /**
     * 当前版本始终为空
     */
    @JsonProperty("retrieval")
    private Map retrieval;

    @JsonProperty("function")
    private Function function;

    @Getter
    @AllArgsConstructor
    public enum Type {
        CODE_INTERPRETER("code_interpreter"),
        RETRIEVAL("retrieval"),
        FUNCTION("function"),
        ;
        private final String name;
    }
}
