package com.hula.ai.llm.locallm.ollama.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ChatResponse implements Serializable {

    private String model;

    @JsonProperty("created_at")
    private String createdAt;

    private ChatCompletionMessage message;

    private String response;

    private Boolean done;

}
