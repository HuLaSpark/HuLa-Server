package com.hula.ai.llm.openai.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Logprobs implements Serializable {
    @JsonProperty("text_offset")
    private List textOffset;
    @JsonProperty("token_logprobs")
    private List tokenLogprobs;
    @JsonProperty("tokens")
    private List tokens;
    @JsonProperty("topLogprobs")
    private List top_logprobs;
}