package com.hula.ai.llm.spark.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 请求参数
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatCompletion implements Serializable {
    private static final long serialVersionUID = -1815416415486571475L;

    /**
     * 指定访问的领域<br/>
     * 必传,取值为 generalv2
     */
    private String domain = "generalv2";

    /**
     * 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高<br/>
     * 非必传,取值为[0,1],默认为0.5
     */
    private Double temperature;

    /**
     * 模型回答的tokens的最大长度<br/>
     * 非必传,取值为[1,4096],默认为2048
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 从k个候选中随机选择⼀个（⾮等概率）<br/>
     * 非必传,取值为[1,6],默认为4
     */
    @JsonProperty("top_k")
    private Integer topK;

    /**
     * 用于关联用户会话<br/>
     * 非必传,需要保障用户下的唯一性
     */
    @JsonProperty("chat_id")
    private String chatId;

}
