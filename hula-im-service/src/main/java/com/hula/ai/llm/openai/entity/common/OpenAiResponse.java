package com.hula.ai.llm.openai.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 描述：
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiResponse<T> implements Serializable {
    private String object;
    private List<T> data;
    private Error error;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Error {
        private String message;
        private String type;
        private String param;
        private String code;
    }
}
