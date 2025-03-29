package com.hula.ai.llm.openai.entity.whisper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhisperResponse implements Serializable {

    private String text;
}
