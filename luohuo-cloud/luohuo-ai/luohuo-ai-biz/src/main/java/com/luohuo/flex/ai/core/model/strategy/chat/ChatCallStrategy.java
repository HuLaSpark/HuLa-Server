package com.luohuo.flex.ai.core.model.strategy.chat;

import java.util.List;
import java.util.Map;

public interface ChatCallStrategy {
    class Result {
		public String content;
		public String reasoning;
    }

    Result call(String baseUrl, String apiKey, String model, List<Map<String, String>> messages, Integer maxTokens, Double temperature) throws Exception;
}
