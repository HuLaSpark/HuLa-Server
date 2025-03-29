package com.hula.ai.llm.openai.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice implements Serializable {
    private String text;
    private long index;
    private Logprobs logprobs;
    @JsonProperty("finish_reason")
    private String finishReason;
}