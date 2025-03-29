package com.hula.ai.llm.spark.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 讯飞星火请求消息信息
 *
 * @author: 云裂痕
 * @date: 2024/05/30
 * @version: 1.1.7
 * 得其道
 * 乾乾
 */
@Data
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 6725091574720504980L;

    private List<ChatCompletionMessage> text;

    public ChatMessage() {
    }

    public ChatMessage(List<ChatCompletionMessage> text) {
        this.text = text;
    }

}
