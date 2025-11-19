package com.luohuo.flex.ai.core.model.openrouter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import reactor.core.publisher.Flux;

/**
 * OpenRouter {@link ChatModel} 实现类
 *
 * <p>OpenRouter 兼容 OpenAI 接口，可以直接复用 OpenAiChatModel</p>
 * <p>API 文档：<a href="https://openrouter.ai/docs/api-reference/overview">OpenRouter API</a></p>
 *
 * @author 乾乾
 */
@Slf4j
@RequiredArgsConstructor
public class OpenRouterChatModel implements ChatModel {

    /**
     * OpenRouter API 基础 URL
     */
    public static final String BASE_URL = OpenRouterApiConstants.DEFAULT_BASE_URL;

    /**
     * 默认模型
     */
    public static final String MODEL_DEFAULT = OpenRouterApiConstants.MODEL_DEFAULT;

    /**
     * 兼容 OpenAI 接口，进行复用
     */
    private final OpenAiChatModel openAiChatModel;

    @Override
    public ChatResponse call(Prompt prompt) {
        return openAiChatModel.call(prompt);
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return openAiChatModel.stream(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return openAiChatModel.getDefaultOptions();
    }

}