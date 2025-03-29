package com.hula.ai.llm.spark.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火请求体
 *
 * @author: 云裂痕
 * @date: 2024/05/30
 * @version: 1.1.7
 * 得其道
 * 乾乾
 */
@Data
public class ChatPayload implements Serializable {
    private static final long serialVersionUID = 2084163918219863102L;

    /**
     * 消息内容
     */
    private ChatMessage message;

    public ChatPayload() {
    }

    public ChatPayload(ChatMessage message) {
        this.message = message;
    }

}
