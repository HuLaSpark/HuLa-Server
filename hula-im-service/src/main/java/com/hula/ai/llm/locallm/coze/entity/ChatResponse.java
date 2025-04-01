package com.hula.ai.llm.locallm.coze.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ChatResponse implements Serializable {

    private String id;

    private String role;

    private String type;

    private String content;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("bot_id")
    private String botId;

    private String status;

    private Usage usage;

    /**
     * 成功状态为0
     */
    private int code;

    private String msg;

}
