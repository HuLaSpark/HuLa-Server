package com.hula.ai.llm.openai.entity.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelResponse implements Serializable {
    private String object;
    private List<Model> data;
}
