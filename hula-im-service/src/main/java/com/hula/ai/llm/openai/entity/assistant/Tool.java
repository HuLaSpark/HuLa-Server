package com.hula.ai.llm.openai.entity.assistant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hula.ai.llm.openai.entity.chat.Functions;
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
public class Tool implements Serializable {

    private String type;

    /**
     * type为function时，function参数必输
     */
    private Functions function;


    /**
     * 支持的种类型
     */
    @Getter
    @AllArgsConstructor
    public enum Type {
        CODE_INTERPRETER("code_interpreter"),
        @Deprecated
        RETRIEVAL("retrieval"),
        FUNCTION("function"),
        FILE_SEARCH("file_search"),
        ;
        private final String name;
    }
}
