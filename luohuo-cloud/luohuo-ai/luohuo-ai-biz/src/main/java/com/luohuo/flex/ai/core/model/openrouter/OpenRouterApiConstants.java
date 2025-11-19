package com.luohuo.flex.ai.core.model.openrouter;

/**
 * OpenRouter API 常量
 *
 * @author 乾乾
 */
public final class OpenRouterApiConstants {

    /**
     * OpenRouter API 基础 URL
     * 注意：OpenAI 客户端会自动添加 /v1，所以这里只用 https://openrouter.ai/api
     */
    public static final String DEFAULT_BASE_URL = "https://openrouter.ai/api";

    /**
     * 默认模型
     */
    public static final String MODEL_DEFAULT = "openai/gpt-3.5-turbo";

    /**
     * 提供商名称
     */
    public static final String PROVIDER_NAME = "OpenRouter";

}
