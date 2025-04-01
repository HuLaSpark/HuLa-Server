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
public class Output implements Serializable {

    /**
     * @see Type
     */
    private String type;

    private String logs;

    private Image image;

    @Getter
    @AllArgsConstructor
    public enum Type {
        LOGS("logs"),
        IMAGE("image"),
        ;
        private final String name;
    }
}
