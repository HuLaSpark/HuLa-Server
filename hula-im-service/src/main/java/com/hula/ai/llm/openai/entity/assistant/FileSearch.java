package com.hula.ai.llm.openai.entity.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FileSearch {

    @JsonProperty("vector_store_ids")
    private List<String> vectorStoreIds;

    @JsonProperty("vector_stores")
    private List<VectorStore> vectorStores;
}
