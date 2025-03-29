package com.hula.ai.llm.openai.entity.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolResources {

    @JsonProperty("code_interpreter")
    private CodeInterpreter codeInterpreter;
    @JsonProperty("code_interpreter")
    private FileSearch fileSearch;
}
