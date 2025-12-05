package com.luohuo.flex.ai.core.model.strategy.chat;

import com.luohuo.flex.ai.core.model.openai.OpenAiCompatClient;

import java.util.List;
import java.util.Map;

public class OpenAiCompatCallStrategy implements ChatCallStrategy {
    @Override
    public Result call(String baseUrl, String apiKey, String model, List<Map<String, String>> messages, Integer maxTokens, Double temperature) throws Exception {
        OpenAiCompatClient client = new OpenAiCompatClient(baseUrl, apiKey);
        OpenAiCompatClient.Result res = client.callChat(model, messages, maxTokens, temperature);
        Result r = new Result();
        r.content = res.content;
        r.reasoning = res.reasoning;
        return r;
    }
}
