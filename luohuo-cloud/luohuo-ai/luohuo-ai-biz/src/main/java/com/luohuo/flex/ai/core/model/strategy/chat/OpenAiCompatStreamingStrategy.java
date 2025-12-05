package com.luohuo.flex.ai.core.model.strategy.chat;

import com.luohuo.flex.ai.core.model.openai.OpenAiCompatSseClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public class OpenAiCompatStreamingStrategy implements ChatStreamingStrategy {
    @Override
    public Flux<ReasoningChunk> stream(String baseUrl, String apiKey, String model, List<Map<String, String>> messages, Integer maxTokens, Double temperature) {
        OpenAiCompatSseClient client = new OpenAiCompatSseClient(baseUrl, apiKey);
        return client.streamChat(model, messages, maxTokens, temperature).map(c -> {
            ReasoningChunk r = new ReasoningChunk();
            r.content = c.content;
            r.reasoning = c.reasoning;
            return r;
        });
    }
}
