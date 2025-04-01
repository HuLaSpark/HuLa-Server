package com.hula.ai.llm.spark.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火请求参数
 *
 * @author: 云裂痕
 * @date: 2024/05/30
 * @version: 1.1.7
 * 得其道
 * 乾乾
 */
@Data
public class ChatParameter implements Serializable {
    private static final long serialVersionUID = 4502096141480336425L;

    private ChatCompletion chat;

    public ChatParameter() {
    }

    public ChatParameter(ChatCompletion chat) {
        this.chat = chat;
    }

}
