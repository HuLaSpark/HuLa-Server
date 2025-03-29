package com.hula.ai.llm.openai.entity.assistant.run;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunError implements Serializable {
    /**
     * One of server_error or rate_limit_exceeded.
     * @see Code
     */
    private String code;
    private String message;


    @Getter
    @AllArgsConstructor
    public enum Code {
        SERVER_ERROR("server_error"),
        RATE_LIMIT_EXCEEDED("rate_limit_exceeded"),
        ;
        private final String name;
    }
}
