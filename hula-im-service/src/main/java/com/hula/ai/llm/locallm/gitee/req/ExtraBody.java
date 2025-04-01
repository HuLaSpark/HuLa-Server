package com.hula.ai.llm.locallm.gitee.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraBody {
    /**
     * 从概率最高的前 k 个词汇中进行采样。
     * 用于限制采样的词汇范围，k 的值为正整数。
     */
    @JsonProperty("top_k")
    private int topK;
}
