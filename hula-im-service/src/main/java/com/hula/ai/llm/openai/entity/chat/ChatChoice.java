package com.hula.ai.llm.openai.entity.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatChoice implements Serializable {

    private long index;
    /**
     * 请求参数stream为true返回是delta
     */
    @JsonProperty("delta")
    private Message delta;
    /**
     * 请求参数stream为false返回是message
     */
    @JsonProperty("message")
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;

    private LogprobContent logprobs;
}
