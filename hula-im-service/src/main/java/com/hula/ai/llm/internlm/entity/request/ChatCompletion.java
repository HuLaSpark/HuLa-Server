package com.hula.ai.llm.internlm.entity.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对话请求
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletion {

    /***
     * 模型
     */
    public String model;

    /**
     * 消息
     */
    public List<ChatCompletionMessage> messages;

    @SerializedName("temperature")
    public float temperature;

    @SerializedName("top_p")
    public float topP;

    @SerializedName("request_output_len")
    public Integer requestOutputLen;

    public boolean stream = false;

}
