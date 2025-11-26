package com.luohuo.flex.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "luohuo.ai")
@Configuration
@Data
public class HulaAiProperties {
    /**
     * DeepSeek
     */
    @SuppressWarnings("SpellCheckingInspection")
    private DeepSeekProperties deepseek;

    /**
     * 字节豆包
     */
    @SuppressWarnings("SpellCheckingInspection")
    private DouBaoProperties doubao;

    /**
     * 腾讯混元
     */
    @SuppressWarnings("SpellCheckingInspection")
    private HunYuanProperties hunyuan;

    /**
     * 硅基流动
     */
    @SuppressWarnings("SpellCheckingInspection")
    private SiliconFlowProperties siliconflow;

    /**
     * 讯飞星火
     */
    @SuppressWarnings("SpellCheckingInspection")
    private XingHuoProperties xinghuo;

    /**
     * 百川
     */
    @SuppressWarnings("SpellCheckingInspection")
    private BaiChuanProperties baichuan;

    /**
     * Midjourney 绘图
     */
    private MidjourneyProperties midjourney;

    /**
     * Suno 音乐
     */
    @SuppressWarnings("SpellCheckingInspection")
    private SunoProperties suno;

    /**
     * OpenRouter
     */
    @SuppressWarnings("SpellCheckingInspection")
    private OpenRouterProperties openrouter;

    @SuppressWarnings("SpellCheckingInspection")
    private GeminiProperties gemini;

    @Data
    public static class DeepSeekProperties {

        private String enable;
        private String apiKey;

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }

    @Data
    public static class DouBaoProperties {

        private String enable;
        private String apiKey;

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }

    @Data
    public static class HunYuanProperties {

        private String enable;
        private String baseUrl;
        private String apiKey;

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }

    @Data
    public static class SiliconFlowProperties {

        private String enable;
        private String apiKey;

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }

    @Data
    public static class XingHuoProperties {

        private String enable;
        private String appId;
        private String appKey;
        private String secretKey;

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }

    @Data
    public static class BaiChuanProperties {

        private String enable;
        private String apiKey;

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }

    @Data
    public static class MidjourneyProperties {

        private String enable;
        private String baseUrl;

        private String apiKey;
        private String notifyUrl;

    }

    @Data
    public static class SunoProperties {

        private boolean enable = false;

        private String baseUrl;

    }

    @Data
    public static class OpenRouterProperties {
        private String enable;
        private String apiKey;
        private String baseUrl;  // 支持自定义 Base URL（可用于代理）

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }

    @Data
    public static class GeminiProperties {

        private String enable;
        private String apiKey;
        private String baseUrl;

        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

    }
}
