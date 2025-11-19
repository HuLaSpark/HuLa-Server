package com.luohuo.flex.ai.core.model.silicon;

import com.luohuo.flex.ai.enums.ErrorCodeConstants;
import com.luohuo.flex.ai.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import reactor.core.publisher.Flux;

/**
 * 硅基流动 {@link ChatModel} 实现类
 *
 * 1. API 文档：<a href="https://docs.siliconflow.cn/cn/api-reference/chat-completions/chat-completions">API 文档</a>
 * 2. 硅基流动的模型有token长度限制，需要在发送请求前进行验证
 */
@Slf4j
@RequiredArgsConstructor
public class SiliconFlowChatModel implements ChatModel {

    /**
     * 兼容 OpenAI 接口，进行复用
     */
    private final OpenAiChatModel openAiChatModel;

    /**
     * Token计数器，用于验证请求长度
     */
    private final TokenCountEstimator tokenCountEstimator;

    /**
     * 硅基流动的最大context window大小（根据模型而定，这里使用保守值）
     * 大多数模型的max_seq_len为32768，但我们预留10k的buffer
     */
    private static final int SILICON_FLOW_MAX_TOKENS = 22768; // 32768 - 10000 buffer

    @Override
    public ChatResponse call(Prompt prompt) {
        validateTokenLength(prompt);
        return openAiChatModel.call(prompt);
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        validateTokenLength(prompt);
        return openAiChatModel.stream(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return openAiChatModel.getDefaultOptions();
    }

    /**
     * 验证prompt的token长度是否超过限制
     * @param prompt 要验证的prompt
     * @throws ServiceException 如果token长度超过限制
     */
    private void validateTokenLength(Prompt prompt) {
        try {
            // 获取所有消息的内容
            StringBuilder allContent = new StringBuilder();
            prompt.getInstructions().forEach(message -> {
                allContent.append(message.getText()).append("\n");
            });

            // 估算token数量
            int estimatedTokens = tokenCountEstimator.estimate(allContent.toString());

            if (estimatedTokens > SILICON_FLOW_MAX_TOKENS) {
                log.warn("SiliconFlow prompt token length {} exceeds max allowed {}",
                    estimatedTokens, SILICON_FLOW_MAX_TOKENS);
                throw new ServiceException(ErrorCodeConstants.CHAT_PROMPT_TOO_LONG);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error validating token length for SiliconFlow", e);
            // 如果token计数失败，继续发送请求，让API返回错误
        }
    }

}
