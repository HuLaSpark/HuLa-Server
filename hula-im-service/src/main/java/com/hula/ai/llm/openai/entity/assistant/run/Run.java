package com.hula.ai.llm.openai.entity.assistant.run;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hula.ai.llm.openai.entity.assistant.Tool;
import com.hula.ai.llm.openai.entity.chat.ChatCompletion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class Run implements Serializable {

    @JsonProperty("assistant_id")
    private String assistantId;

    /**
     * All models except gpt-3.5-turbo-0301 supported. retrieval tool requires gpt-4-1106-preview or gpt-3.5-turbo-1106.
     *
     * @see ChatCompletion.Model
     */
    private String model;
    /**
     * 助手使用的系统指令。最大长度为 32768 个字符。
     */
    private String instructions;
    /**
     * 助手上启用的工具列表。每个助手最多可以有 128 个工具。工具可以是 code_interpreter、retrieval或function。
     */
    private List<Tool> tools;
    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long and values can be a maxium of 512 characters long.
     */
    private Map metadata;

    private boolean stream;
}
