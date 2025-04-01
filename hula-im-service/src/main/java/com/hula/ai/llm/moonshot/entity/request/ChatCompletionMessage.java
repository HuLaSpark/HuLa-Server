package com.hula.ai.llm.moonshot.entity.request;

import lombok.Data;

/**
 * 对话内容
 *
 * @author: 云裂痕
 * @date: 2025/03/09
 * @version: 1.2.0
 * 得其道 乾乾
 */
@Data
public class ChatCompletionMessage {

    /**
     * 角色
     */
    public String role;
    public String name;
    public String content;
    public Boolean partial;

    public ChatCompletionMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public ChatCompletionMessage(String role, String name, String content, Boolean partial) {
        this.role = role;
        this.name = name;
        this.content = content;
        this.partial = partial;
    }

}
