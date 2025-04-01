package com.hula.ai.llm.internlm.entity.response;

import com.google.gson.annotations.SerializedName;
import com.hula.ai.llm.internlm.entity.request.ChatCompletionMessage;
import lombok.Data;

/**
 * 返回内容
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatChoice {


    private int index;

    /**
     * 对话内容
     */
    private ChatCompletionMessage message;

    /**
     * 结束原因
     */
    @SerializedName("finish_reason")
    private String finishReason;

}
