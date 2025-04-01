package com.hula.ai.llm.openai.entity.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CodeInterpreter {

    @JsonProperty("file_ids")
    private List<String> fileIds;
}
