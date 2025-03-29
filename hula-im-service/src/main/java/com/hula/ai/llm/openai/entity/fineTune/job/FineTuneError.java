package com.hula.ai.llm.openai.entity.fineTune.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FineTuneError {
    private String message;
    private String param;
    private String code;
}
