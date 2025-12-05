package com.luohuo.flex.ai.core.model.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用 OpenAI 兼容 SSE 客户端
 * 可用于 Gitee AI、SiliconFlow、OpenAI、OpenRouter 等提供 OpenAI 兼容接口的平台
 */
public class OpenAiCompatSseClient {

    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAiCompatSseClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl == null || baseUrl.isBlank() ? "https://api.openai.com" : baseUrl;
        this.apiKey = apiKey;
    }

    public static class Chunk {
        public String content;
        public String reasoning;
        public boolean done;
    }

    public Flux<Chunk> streamChat(String model,
                                  List<Map<String, String>> messages,
                                  Integer maxTokens,
                                  Double temperature) {
        return Flux.create(sink -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("model", model);
                body.put("messages", messages);
                body.put("stream", true);
                if (maxTokens != null) body.put("max_tokens", maxTokens);
                if (temperature != null) body.put("temperature", temperature);

                String json = mapper.writeValueAsString(body);

                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(20))
                        .build();
                URI uri = URI.create(baseUrl.endsWith("/") ? baseUrl + "v1/chat/completions" : baseUrl + "/v1/chat/completions");
                HttpRequest request = HttpRequest.newBuilder(uri)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .header("Accept", "text/event-stream")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                        .thenAccept(response -> {
                            int status = response.statusCode();
                            if (status < 200 || status >= 300) {
                                sink.error(new IllegalStateException("OpenAI-Compat SSE HTTP status: " + status));
                                return;
                            }
                            try (InputStream is = response.body();
                                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (!line.startsWith("data:")) continue;
                                    String payload = line.substring(5).trim();
                                    if (payload.equals("[DONE]")) {
                                        Chunk c = new Chunk();
                                        c.done = true;
                                        sink.next(c);
                                        sink.complete();
                                        break;
                                    }
                                    try {
                                        JsonNode root = mapper.readTree(payload);
                                        JsonNode choices = root.path("choices");
                                        if (choices.isArray() && choices.size() > 0) {
                                            JsonNode delta = choices.get(0).path("delta");
                                            String content = delta.path("content").isMissingNode() ? null : delta.path("content").asText(null);
                                            // 兼容不同平台的推理字段命名
                                            String reasoning = null;
                                            if (!delta.path("reasoning_content").isMissingNode()) {
                                                reasoning = delta.path("reasoning_content").asText(null);
                                            } else if (!delta.path("reasoning").isMissingNode()) {
                                                reasoning = delta.path("reasoning").asText(null);
                                            } else if (!delta.path("thinking").isMissingNode()) {
                                                reasoning = delta.path("thinking").asText(null);
                                            } else if (!delta.path("deliberate_output").isMissingNode()) {
                                                reasoning = delta.path("deliberate_output").asText(null);
                                            } else if (!delta.path("thoughts").isMissingNode()) {
                                                reasoning = delta.path("thoughts").asText(null);
                                            }
                                            Chunk c = new Chunk();
                                            c.content = content;
                                            c.reasoning = reasoning;
                                            sink.next(c);
                                        }
                                    } catch (Exception e) {
                                        sink.error(e);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                sink.error(e);
                            }
                        })
                        .exceptionally(e -> {
                            sink.error(e);
                            return null;
                        });
            } catch (Exception e) {
                sink.error(e);
            }
        }, FluxSink.OverflowStrategy.BUFFER);
    }
}
