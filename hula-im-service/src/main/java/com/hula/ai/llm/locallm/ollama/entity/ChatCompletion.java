package com.hula.ai.llm.locallm.ollama.entity;

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
     * 模型
     */
    private String model;

    /**
     * 提示
     */
    private String prompt;

    /**
     * 聊天内容
     */
    private List<ChatCompletionMessage> messages;

}


