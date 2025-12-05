package com.luohuo.flex.ai.core.model.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用 OpenAI 兼容 同步客户端（非流式）
 * 读取单次返回中的 message.content 与 message.reasoning_content
 */
public class OpenAiCompatClient {

    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAiCompatClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl == null || baseUrl.isBlank() ? "https://api.openai.com" : baseUrl;
        this.apiKey = apiKey;
    }

    public static class Result {
        public String content;
        public String reasoning;
    }

    public Result callChat(String model,
                           List<Map<String, String>> messages,
                           Integer maxTokens,
                           Double temperature) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        if (maxTokens != null) body.put("max_tokens", maxTokens);
        if (temperature != null) body.put("temperature", temperature);

        String json = mapper.writeValueAsString(body);

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        URI uri = URI.create(baseUrl.endsWith("/") ? baseUrl + "v1/chat/completions" : baseUrl + "/v1/chat/completions");
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IllegalStateException("OpenAI-Compat HTTP status: " + resp.statusCode());
        }
        JsonNode root = mapper.readTree(resp.body());
        JsonNode choices = root.path("choices");
        Result r = new Result();
        if (choices.isArray() && choices.size() > 0) {
            JsonNode msg = choices.get(0).path("message");
            r.content = msg.path("content").isMissingNode() ? null : msg.path("content").asText(null);
            // 兼容不同命名
            if (!msg.path("reasoning_content").isMissingNode()) {
                r.reasoning = msg.path("reasoning_content").asText(null);
            } else if (!msg.path("reasoning").isMissingNode()) {
                r.reasoning = msg.path("reasoning").asText(null);
            } else if (!msg.path("thinking").isMissingNode()) {
                r.reasoning = msg.path("thinking").asText(null);
            } else if (!msg.path("deliberate_output").isMissingNode()) {
                r.reasoning = msg.path("deliberate_output").asText(null);
            } else if (!msg.path("thoughts").isMissingNode()) {
                r.reasoning = msg.path("thoughts").asText(null);
            }
        }
        return r;
    }
}
