package com.hula.ai.llm.locallm.langchain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hula.ai.llm.locallm.base.BaseChatCompletion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@Slf4j
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatCompletion extends BaseChatCompletion {

    private String query;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("history_len")
    private Integer historyLen = -1;

    @JsonProperty("knowledge_base_name")
    private String knowledgeBaseName;

    @JsonProperty("top_k")
    private Integer topK = 3;

    @JsonProperty("score_threshold")
    private Integer scoreThreshold = 1;

    private List<ChatCompletionMessage> history;

    private String modelName;

    private Double temperature = 0.7;

    @JsonProperty("max_tokens")
    private Integer maxTokens = 0;

    @JsonProperty("prompt_name")
    private String promptName = "default";

}


