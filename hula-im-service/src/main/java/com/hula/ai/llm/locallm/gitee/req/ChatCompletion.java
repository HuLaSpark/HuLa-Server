package com.hula.ai.llm.locallm.gitee.req;

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

    /**
     * 要使用的聊天模型名称。
     * 例如："DeepSeek-R1-Distill-Qwen-32B"，用于指定具体调用的模型。
     */
    @JsonProperty("model")
    private String model;

    /**
     * 是否以流式方式返回结果。
     * 如果设置为 true，则模型会逐步返回生成的文本，适合实时交互场景；
     * 如果设置为 false，则会等待整个生成结果完成后一次性返回。
     */
//    @JsonProperty("stream")
//    private boolean stream;

    /**
     * 生成结果的最大令牌数。
     * 用于限制模型生成文本的长度，避免生成过长的内容。
     */
    @JsonProperty("max_tokens")
    private int maxTokens;

    /**
     * 温度参数，控制生成文本的随机性。
     * 值越高，生成的文本越随机、多样化；值越低，生成的文本越确定、保守。
     * 取值范围通常在 0 到 1 之间。
     */
    @JsonProperty("temperature")
    private double temperature;

    /**
     * 核采样概率，用于控制生成文本的多样性。
     * 模型会从概率最高的前 p 部分的词汇中进行采样，p 的值通常在 0 到 1 之间。
     */
    @JsonProperty("top_p")
    private double topP;

    @JsonProperty("extraBody")
    private ExtraBody extraBody;

    /**
     * 频率惩罚因子，用于控制重复词汇的生成。
     * 值越大，模型越倾向于避免使用已经出现过的词汇，可减少重复内容的生成。
     */
    @JsonProperty("frequency_penalty")
    private double frequencyPenalty;

    /**
     * 对话消息数组，包含系统消息和用户消息等。
     * 每个消息对象包含角色（如 "system"、"user"）和具体内容。
     * 模型会根据这些消息进行回复。
     */
    @JsonProperty("messages")
    private List<Message> messages;
}