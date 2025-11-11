package com.luohuo.flex.ai.core.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import reactor.core.publisher.Flux;

/**
 * 字节豆包 {@link ChatModel} 实现类
 *
 * @author 乾乾
 */
@Slf4j
@RequiredArgsConstructor
public class DouBaoChatModel implements ChatModel {

    public static final String BASE_URL = "https://ark.cn-beijing.volces.com/api";

    public static final String MODEL_DEFAULT = "doubao-1-5-lite-32k-250115";

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
