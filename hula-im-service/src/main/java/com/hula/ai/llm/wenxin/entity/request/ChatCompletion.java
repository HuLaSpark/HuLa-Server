package com.hula.ai.llm.wenxin.entity.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 聊天信息
 *
 * @author: 云裂痕
 * @date: 2023/9/7
 * 得其道
 * 乾乾
 */
@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatCompletion implements Serializable {
    @NonNull
    private List<ChatCompletionMessage> messages;

    @Builder.Default
    private Float temperature = 0.2f;

    @JsonProperty("top_p")
    @Builder.Default
    private Float topP = 1.0f;

    @JsonProperty("penalty_score")
    @Builder.Default
    private Float penaltyScore = 1.0f;

    @Builder.Default
    private Boolean stream = false;

    @JsonProperty("user_id")
    private String userId;
}


