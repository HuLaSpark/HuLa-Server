package com.hula.ai.llm.openai.entity.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hula.ai.llm.openai.entity.common.Usage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse implements Serializable {

    private String object;
    private List<Item> data;
    private String model;
    private Usage usage;
}
