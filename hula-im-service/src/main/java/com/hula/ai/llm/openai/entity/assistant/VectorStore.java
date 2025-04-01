package com.hula.ai.llm.openai.entity.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class VectorStore {

    @JsonProperty("file_ids")
    private List<String> fileIds;

    @JsonProperty("metadata")
    private Map metadata;
}
