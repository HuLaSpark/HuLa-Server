package com.hula.ai.llm.internlm.entity.response;

import lombok.Data;

import java.util.List;

/**
 * 输出返回
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatResponse {

    private String id;

    private String object;

    private long created;

    /**
     * 模型
     */
    private String model;

    /**
     * 对话内容
     */
    private List<ChatChoice> choices;

    /**
     * 使用量
     */
    private Usage usage;

    /**
     * 使用量
     */
    private ChatTokenData data;

}
