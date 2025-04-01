package com.hula.ai.llm.internlm.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话内容
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
public class ChatCompletionMessage {

    /**
     * 角色
     */
    public String role;

    /**
     * 内容
     */
    public String text;

}
