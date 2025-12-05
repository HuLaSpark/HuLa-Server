package com.luohuo.flex.ai.core.model.strategy.chat;

import com.luohuo.flex.ai.enums.AiPlatformEnum;
import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.ai.core.model.openrouter.OpenRouterApiConstants;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowApiConstants;

public class ChatStrategyFactory {
    public static ChatStreamingStrategy getStreamingStrategy(AiPlatformEnum platform) {
        if (platform == AiPlatformEnum.DEEP_SEEK) {
            return new DeepSeekStreamingStrategy();
        }
        if (platform == AiPlatformEnum.GITEE_AI || platform == AiPlatformEnum.SILICON_FLOW || platform == AiPlatformEnum.OPENAI || platform == AiPlatformEnum.OPENROUTER) {
            return new OpenAiCompatStreamingStrategy();
        }
        return null;
    }

    public static ChatCallStrategy getCallStrategy(AiPlatformEnum platform) {
        if (platform == AiPlatformEnum.DEEP_SEEK || platform == AiPlatformEnum.GITEE_AI || platform == AiPlatformEnum.SILICON_FLOW || platform == AiPlatformEnum.OPENAI || platform == AiPlatformEnum.OPENROUTER) {
            return new OpenAiCompatCallStrategy();
        }
        return null;
    }

    public static String resolveBaseUrl(AiPlatformEnum platform, String apiKeyUrl) {
        if (StrUtil.isNotBlank(apiKeyUrl)) return apiKeyUrl;
        if (platform == AiPlatformEnum.DEEP_SEEK) return "https://api.deepseek.com";
        if (platform == AiPlatformEnum.GITEE_AI) return "https://ai.gitee.com";
        if (platform == AiPlatformEnum.SILICON_FLOW) return SiliconFlowApiConstants.DEFAULT_BASE_URL;
        if (platform == AiPlatformEnum.OPENROUTER) return OpenRouterApiConstants.DEFAULT_BASE_URL;
        return "https://api.openai.com";
    }

    public static String normalizeModel(AiPlatformEnum platform, String model, Boolean reasoningEnabled) {
        String effective = model;
        if (Boolean.TRUE.equals(reasoningEnabled)) {
            if (platform == AiPlatformEnum.DEEP_SEEK || platform == AiPlatformEnum.HUN_YUAN && StrUtil.startWithIgnoreCase(effective, "deepseek")) {
                effective = "deepseek-reasoner";
            }
        }
        return effective;
    }
}
