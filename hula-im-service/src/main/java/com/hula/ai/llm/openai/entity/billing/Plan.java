package com.hula.ai.llm.openai.entity.billing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan {
    private String title;
    private String id;
}
