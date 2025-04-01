package com.hula.ai.llm.openai.entity.completions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hula.ai.llm.openai.entity.common.Choice;
import com.hula.ai.llm.openai.entity.common.OpenAiResponse;
import com.hula.ai.llm.openai.entity.common.Usage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述： 答案类
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponse extends OpenAiResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String warning;
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
}
