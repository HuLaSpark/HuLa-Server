package com.luohuo.flex.ai.core.model.google;

import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

public class GeminiApi {
    private final RestClient client;
    private final String apiKey;
    private final String baseUrl;

    public GeminiApi(String baseUrl, String apiKey) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public GenerateResponse generateContent(String model, GenerateRequest request) {
        String path = "/models/" + model + ":generateContent";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", apiKey);
        return client.post()
                .uri(builder -> builder.path(path).queryParams(params).build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GenerateResponse.class);
    }

    @Data
    public static class GenerateRequest {
        private List<Content> contents;
        private GenerationConfig generationConfig;
        private SystemInstruction systemInstruction;
    }

    @Data
    public static class SystemInstruction {
        private List<Part> parts;
    }

    @Data
    public static class Content {
        private String role;
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
        private InlineData inlineData;
        private Map<String, Object> fileData;
    }

    @Data
    public static class InlineData {
        private String mimeType;
        private String data;
    }

    @Data
    public static class GenerationConfig {
        private Double temperature;
        private Integer maxOutputTokens;
        private Double topP;
    }

    @Data
    public static class GenerateResponse {
        private List<Candidate> candidates;
    }

    @Data
    public static class Candidate {
        private Content content;
    }
}