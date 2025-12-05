package com.luohuo.flex.ai.core.model.strategy.chat;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface ChatStreamingStrategy {
	Flux<ReasoningChunk> stream(String baseUrl, String apiKey, String model, List<Map<String, String>> messages, Integer maxTokens, Double temperature);
}
