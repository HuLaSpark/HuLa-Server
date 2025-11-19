package com.luohuo.flex.ai.core.model.silicon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Slf4j
public class SiliconFlowAudioApi {

    private static final String BASE_URL = "https://api.siliconflow.cn";
    private static final String AUDIO_SPEECH_URL = BASE_URL + "/v1/audio/speech";

    // 支持的模型列表
    private static final Set<String> SUPPORTED_MODELS = Set.of(
            "fnlp/MOSS-TTSD-v0.5",
            "CosyVoice2-0.5B"
    );

    private final String apiKey;
    private final RestTemplate restTemplate;

    public SiliconFlowAudioApi(String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public byte[] createSpeech(AudioRequest request) {
        // 1. 验证模型是否支持
        validateModel(request.getModel());

        // 2. 验证音色是否支持
        validateVoice(request.getModel(), request.getVoice());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 使用 ObjectMapper 将请求对象序列化为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    AUDIO_SPEECH_URL,
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("音频生成失败: " + response.getStatusCode());
            }

            log.info("[createSpeech] 音频生成成功，大小: {} bytes", response.getBody().length);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("[createSpeech] API 调用失败: {}", errorBody);
            throw new RuntimeException("音频生成失败: " + errorBody);
        }
    }

    private void validateModel(String model) {
        if (!SUPPORTED_MODELS.contains(model)) {
            String supportedModels = String.join(", ", SUPPORTED_MODELS);
            String errorMsg = String.format(
                    "%s",
                    model, supportedModels
            );
            log.error("[validateModel] {}", errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * 验证音色是否支持该模型
     */
    private void validateVoice(String model, String voice) {
        Set<String> supportedVoices = getSupportedVoices(model);
        if (supportedVoices != null && !supportedVoices.isEmpty() && !supportedVoices.contains(voice)) {
            String supportedVoiceList = String.join(", ", supportedVoices);
            String errorMsg = String.format(
                    "模型 %s 不支持音色 %s，支持的音色: %s",
                    model, voice, supportedVoiceList
            );
            log.error("[validateVoice] {}", errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    public static Set<String> getSupportedVoices(String model) {
        if ("fnlp/MOSS-TTSD-v0.5".equals(model)) {
            return Set.of(
                    "fnlp/MOSS-TTSD-v0.5:alex",
                    "fnlp/MOSS-TTSD-v0.5:anna",
                    "fnlp/MOSS-TTSD-v0.5:bella",
                    "fnlp/MOSS-TTSD-v0.5:benjamin",
                    "fnlp/MOSS-TTSD-v0.5:charles",
                    "fnlp/MOSS-TTSD-v0.5:claire",
                    "fnlp/MOSS-TTSD-v0.5:david",
                    "fnlp/MOSS-TTSD-v0.5:diana"
            );
        } else if ("CosyVoice2-0.5B".equals(model) || "FunAudioLLM/CosyVoice2-0.5B".equals(model)) {
            return null;
        }
        return null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AudioRequest {
        private String model;
        private String input;
        private String voice;

        @JsonProperty("response_format")
        private String responseFormat;

        private Double speed;

        public AudioRequest(String model, String input) {
            this.model = model;
            this.input = input;
            // 根据模型设置默认音色
            this.voice = getDefaultVoiceForModel(model);
            this.responseFormat = "mp3";
            this.speed = 1.0;
        }

        /**
         * 根据模型获取默认音色
         */
        private static String getDefaultVoiceForModel(String model) {
            if ("fnlp/MOSS-TTSD-v0.5".equals(model)) {
                // MOSS-TTSD 模型使用 anna 音色
                return "fnlp/MOSS-TTSD-v0.5:anna";
            } else if ("CosyVoice2-0.5B".equals(model)) {
                // CosyVoice2 模型使用默认音色
                return "default";
            }
            return "default";
        }
    }
}