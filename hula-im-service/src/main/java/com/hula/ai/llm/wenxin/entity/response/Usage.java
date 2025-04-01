package com.hula.ai.llm.wenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 使用量
 *
 * @author: 云裂痕
 * @date: 2023/9/7
 * 得其道
 * 乾乾
 */
@Data
public class Usage {
    @JsonProperty("prompt_tokens")
    private long promptTokens;
    @JsonProperty("completion_tokens")
    private long completionTokens;
    @JsonProperty("total_tokens")
    private long totalTokens;
}
