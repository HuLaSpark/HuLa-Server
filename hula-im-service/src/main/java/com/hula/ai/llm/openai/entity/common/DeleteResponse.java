package com.hula.ai.llm.openai.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteResponse implements Serializable {
    private String id;
    private String object;
    private boolean deleted;
}
