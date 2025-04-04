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
import java.util.List;

@Data
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepDetail implements Serializable {

    /**
     * @see Type
     */
    private String type;

    @JsonProperty("message_creation")
    private MessageCreation messageCreation;

    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    @Getter
    @AllArgsConstructor
    public enum Type {
        MESSAGE_CREATION("message_creation"),
        TOOL_CALLS("tool_calls"),
        ;
        private final String name;
    }
}
