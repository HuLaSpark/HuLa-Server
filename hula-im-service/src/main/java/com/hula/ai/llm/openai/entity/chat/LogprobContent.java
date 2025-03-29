package com.hula.ai.llm.openai.entity.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogprobContent implements Serializable {

    private List<Logprob> content;
}
