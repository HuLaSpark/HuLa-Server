package com.luohuo.flex.ai.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAutoConfiguration;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.luohuo.flex.ai.config.AiAutoConfiguration;
import com.luohuo.flex.ai.config.HulaAiProperties;
import com.luohuo.flex.ai.core.model.BaiChuanChatModel;
import com.luohuo.flex.ai.core.model.DeepSeekChatModel;
import com.luohuo.flex.ai.core.model.DouBaoChatModel;
import com.luohuo.flex.ai.core.model.HunYuanChatModel;
import com.luohuo.flex.ai.core.model.MidjourneyApi;
import com.luohuo.flex.ai.core.model.SunoApi;
import com.luohuo.flex.ai.core.model.XingHuoChatModel;
import com.luohuo.flex.ai.core.model.audio.AudioModel;
import com.luohuo.flex.ai.core.model.openrouter.OpenRouterChatModel;
import com.luohuo.flex.ai.core.model.silicon.*;
import com.luohuo.flex.ai.core.model.gitee.GiteeAiAudioApi;
import com.luohuo.flex.ai.core.model.gitee.GiteeAiAudioModel;
import com.luohuo.flex.ai.core.model.gitee.GiteeAiVideoApi;
import com.luohuo.flex.ai.core.model.gitee.GiteeAiVideoModel;
import com.luohuo.flex.ai.core.model.video.VideoModel;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.basic.utils.collection.CollectionUtils;
import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.SneakyThrows;
import org.springframework.ai.autoconfigure.azure.openai.AzureOpenAiAutoConfiguration;
import org.springframework.ai.autoconfigure.azure.openai.AzureOpenAiChatProperties;
import org.springframework.ai.autoconfigure.azure.openai.AzureOpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.azure.openai.AzureOpenAiEmbeddingProperties;
import org.springframework.ai.autoconfigure.minimax.MiniMaxAutoConfiguration;
import org.springframework.ai.autoconfigure.moonshot.MoonshotAutoConfiguration;
import org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.ai.autoconfigure.qianfan.QianFanAutoConfiguration;
import org.springframework.ai.autoconfigure.stabilityai.StabilityAiImageAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientConnectionDetails;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.autoconfigure.vectorstore.qdrant.QdrantVectorStoreAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.qdrant.QdrantVectorStoreProperties;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.autoconfigure.zhipuai.ZhiPuAiAutoConfiguration;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.MiniMaxEmbeddingModel;
import org.springframework.ai.minimax.MiniMaxEmbeddingOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.model.function.FunctionCallbackResolver;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.ai.moonshot.MoonshotChatOptions;
import org.springframework.ai.moonshot.api.MoonshotApi;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.qianfan.QianFanEmbeddingModel;
import org.springframework.ai.qianfan.QianFanEmbeddingOptions;
import org.springframework.ai.qianfan.QianFanImageModel;
import org.springframework.ai.qianfan.api.QianFanApi;
import org.springframework.ai.qianfan.api.QianFanImageApi;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.observation.DefaultVectorStoreObservationConvention;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.zhipuai.*;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.web.client.RestClient;
import redis.clients.jedis.JedisPooled;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static org.springframework.ai.retry.RetryUtils.DEFAULT_RETRY_TEMPLATE;

/**
 * AI Model 模型工厂的实现类
 *
 * @author 乾乾
 */
public class AiModelFactoryImpl implements AiModelFactory {
    @Override
    public ChatModel getOrCreateChatModel(AiPlatformEnum platform, String apiKey, String url) {
        String cacheKey = buildClientCacheKey(ChatModel.class, platform, apiKey, url);
        return switch (platform) {
            case TONG_YI -> buildTongYiChatModel(apiKey);
            case YI_YAN -> buildYiYanChatModel(apiKey);
            case DEEP_SEEK -> buildDeepSeekChatModel(apiKey);
            case DOU_BAO -> buildDouBaoChatModel(apiKey);
            case HUN_YUAN -> buildHunYuanChatModel(apiKey, url);
            case SILICON_FLOW -> buildSiliconFlowChatModel(apiKey);
            case ZHI_PU -> buildZhiPuChatModel(apiKey, url);
            case MINI_MAX -> buildMiniMaxChatModel(apiKey, url);
            case MOONSHOT -> buildMoonshotChatModel(apiKey, url);
            case XING_HUO -> buildXingHuoChatModel(apiKey);
            case BAI_CHUAN -> buildBaiChuanChatModel(apiKey);
            case GITEE_AI -> buildGiteeAiChatModel(apiKey);
            case OPENROUTER -> buildOpenRouterChatModel(apiKey); // OpenRouter 兼容 OpenAI 接口
            case OPENAI -> buildOpenAiChatModel(apiKey, url);
            case AZURE_OPENAI -> buildAzureOpenAiChatModel(apiKey, url);
            case OLLAMA -> buildOllamaChatModel(url);
            case GEMINI -> buildGeminiChatModel(apiKey, url);
            default -> throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
        };
    }

    @Override
    public ChatModel getDefaultChatModel(AiPlatformEnum platform) {
        // noinspection EnhancedSwitchMigration
        switch (platform) {
            case TONG_YI:
                return SpringUtil.getBean(DashScopeChatModel.class);
            case YI_YAN:
                return SpringUtil.getBean(QianFanChatModel.class);
            case DEEP_SEEK:
                return SpringUtil.getBean(DeepSeekChatModel.class);
            case DOU_BAO:
                return SpringUtil.getBean(DouBaoChatModel.class);
            case HUN_YUAN:
                return SpringUtil.getBean(HunYuanChatModel.class);
            case SILICON_FLOW:
                return SpringUtil.getBean(SiliconFlowChatModel.class);
            case ZHI_PU:
                return SpringUtil.getBean(ZhiPuAiChatModel.class);
            case MINI_MAX:
                return SpringUtil.getBean(MiniMaxChatModel.class);
            case MOONSHOT:
                return SpringUtil.getBean(MoonshotChatModel.class);
            case XING_HUO:
                return SpringUtil.getBean(XingHuoChatModel.class);
            case BAI_CHUAN:
                return SpringUtil.getBean(AzureOpenAiChatModel.class);
            case OPENROUTER:
                return SpringUtil.getBean(OpenRouterChatModel.class);
            case OPENAI:
                return SpringUtil.getBean(OpenAiChatModel.class);
            case AZURE_OPENAI:
                return SpringUtil.getBean(AzureOpenAiChatModel.class);
            case OLLAMA:
                return SpringUtil.getBean(OllamaChatModel.class);
            case GEMINI:
                return SpringUtil.getBean(com.luohuo.flex.ai.core.model.google.GeminiChatModel.class);
            default:
                throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
        }
    }

    @Override
    public ImageModel getDefaultImageModel(AiPlatformEnum platform) {
        // noinspection EnhancedSwitchMigration
        switch (platform) {
            case TONG_YI:
                return SpringUtil.getBean(DashScopeImageModel.class);
            case YI_YAN:
                return SpringUtil.getBean(QianFanImageModel.class);
            case ZHI_PU:
                return SpringUtil.getBean(ZhiPuAiImageModel.class);
            case SILICON_FLOW:
                return SpringUtil.getBean(SiliconFlowImageModel.class);
            case OPENAI:
                return SpringUtil.getBean(OpenAiImageModel.class);
            case STABLE_DIFFUSION:
                return SpringUtil.getBean(StabilityAiImageModel.class);
            default:
                throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
        }
    }

    @Override
    public ImageModel getOrCreateImageModel(AiPlatformEnum platform, String apiKey, String url) {
        // noinspection EnhancedSwitchMigration
        switch (platform) {
            case TONG_YI:
                return buildTongYiImagesModel(apiKey);
            case YI_YAN:
                return buildQianFanImageModel(apiKey);
            case ZHI_PU:
                return buildZhiPuAiImageModel(apiKey, url);
            case OPENAI:
                return buildOpenAiImageModel(apiKey, url);
            case GITEE_AI:
                return buildGiteeAiImageModel(apiKey);
            case SILICON_FLOW:
                return buildSiliconFlowImageModel(apiKey,url);
            case STABLE_DIFFUSION:
                return buildStabilityAiImageModel(apiKey, url);
            default:
                throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
        }
    }

    @Override
    public MidjourneyApi getOrCreateMidjourneyApi(String apiKey, String url) {
        String cacheKey = buildClientCacheKey(MidjourneyApi.class, AiPlatformEnum.MIDJOURNEY.getPlatform(), apiKey,
                url);
        return Singleton.get(cacheKey, (Func0<MidjourneyApi>) () -> {
            HulaAiProperties.MidjourneyProperties properties = SpringUtil.getBean(HulaAiProperties.class)
                    .getMidjourney();
            return new MidjourneyApi(url, apiKey, properties.getNotifyUrl());
        });
    }

    @Override
    public SunoApi getOrCreateSunoApi(String apiKey, String url) {
        String cacheKey = buildClientCacheKey(SunoApi.class, AiPlatformEnum.SUNO.getPlatform(), apiKey, url);
        return Singleton.get(cacheKey, (Func0<SunoApi>) () -> new SunoApi(url));
    }

    @Override
    @SuppressWarnings("EnhancedSwitchMigration")
    public EmbeddingModel getOrCreateEmbeddingModel(AiPlatformEnum platform, String apiKey, String url, String model) {
        String cacheKey = buildClientCacheKey(EmbeddingModel.class, platform, apiKey, url, model);
        return Singleton.get(cacheKey, (Func0<EmbeddingModel>) () -> {
            switch (platform) {
                case TONG_YI:
                    return buildTongYiEmbeddingModel(apiKey, model);
                case YI_YAN:
                    return buildYiYanEmbeddingModel(apiKey, model);
                case ZHI_PU:
                    return buildZhiPuEmbeddingModel(apiKey, url, model);
                case MINI_MAX:
                    return buildMiniMaxEmbeddingModel(apiKey, url, model);
                case OPENAI:
                    return buildOpenAiEmbeddingModel(apiKey, url, model);
                case AZURE_OPENAI:
                    return buildAzureOpenAiEmbeddingModel(apiKey, url, model);
                case OLLAMA:
                    return buildOllamaEmbeddingModel(url, model);
                default:
                    throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
            }
        });
    }

    @Override
    public VectorStore getOrCreateVectorStore(Class<? extends VectorStore> type,
                                              EmbeddingModel embeddingModel,
                                              Map<String, Class<?>> metadataFields) {
        String cacheKey = buildClientCacheKey(VectorStore.class, embeddingModel, type);
        return Singleton.get(cacheKey, (Func0<VectorStore>) () -> {
            if (type == SimpleVectorStore.class) {
                return buildSimpleVectorStore(embeddingModel);
            }
            if (type == QdrantVectorStore.class) {
                return buildQdrantVectorStore(embeddingModel);
            }
            if (type == RedisVectorStore.class) {
                return buildRedisVectorStore(embeddingModel, metadataFields);
            }
            if (type == MilvusVectorStore.class) {
                return buildMilvusVectorStore(embeddingModel);
            }
            throw new IllegalArgumentException(StrUtil.format("未知类型({})", type));
        });
    }

    private static String buildClientCacheKey(Class<?> clazz, Object... params) {
        if (ArrayUtil.isEmpty(params)) {
            return clazz.getName();
        }
        return StrUtil.format("{}#{}", clazz.getName(), ArrayUtil.join(params, "_"));
    }

    // ========== 各种创建 spring-ai 客户端的方法 ==========

    /**
     * 可参考 {@link DashScopeAutoConfiguration} 的 dashscopeChatModel 方法
     */
    private static DashScopeChatModel buildTongYiChatModel(String key) {
        DashScopeApi dashScopeApi = new DashScopeApi(key);
        DashScopeChatOptions options = DashScopeChatOptions.builder().withModel(DashScopeApi.DEFAULT_CHAT_MODEL)
                .withTemperature(0.7).build();
        return new DashScopeChatModel(dashScopeApi, options, getFunctionCallbackResolver(), DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 可参考 {@link DashScopeAutoConfiguration} 的 dashScopeImageModel 方法
     */
    private static DashScopeImageModel buildTongYiImagesModel(String key) {
        DashScopeImageApi dashScopeImageApi = new DashScopeImageApi(key);
        return new DashScopeImageModel(dashScopeImageApi);
    }

    /**
     * 可参考 {@link QianFanAutoConfiguration} 的 qianFanChatModel 方法
     */
    private static QianFanChatModel buildYiYanChatModel(String key) {
        List<String> keys = StrUtil.split(key, '|');
        Assert.equals(keys.size(), 2, "YiYanChatClient 的密钥需要 (appKey|secretKey) 格式");
        String appKey = keys.get(0);
        String secretKey = keys.get(1);
        QianFanApi qianFanApi = new QianFanApi(appKey, secretKey);
        return new QianFanChatModel(qianFanApi);
    }

    /**
     * 可参考 {@link QianFanAutoConfiguration} 的 qianFanImageModel 方法
     */
    private QianFanImageModel buildQianFanImageModel(String key) {
        List<String> keys = StrUtil.split(key, '|');
        Assert.equals(keys.size(), 2, "YiYanChatClient 的密钥需要 (appKey|secretKey) 格式");
        String appKey = keys.get(0);
        String secretKey = keys.get(1);
        QianFanImageApi qianFanApi = new QianFanImageApi(appKey, secretKey);
        return new QianFanImageModel(qianFanApi);
    }

    /**
     * 可参考 {@link AiAutoConfiguration#deepSeekChatModel(HulaAiProperties)}
     */
    private static DeepSeekChatModel buildDeepSeekChatModel(String apiKey) {
        HulaAiProperties.DeepSeekProperties properties = new HulaAiProperties.DeepSeekProperties();
        properties.setApiKey(apiKey);
        return new AiAutoConfiguration().buildDeepSeekChatModel(properties);
    }

    /**
     * 可参考 {@link AiAutoConfiguration#douBaoChatClient(HulaAiProperties)}
     */
    private ChatModel buildDouBaoChatModel(String apiKey) {
        HulaAiProperties.DouBaoProperties properties = new HulaAiProperties.DouBaoProperties();
        properties.setApiKey(apiKey);
        return new AiAutoConfiguration().buildDouBaoChatClient(properties);
    }

    /**
     * 可参考 {@link AiAutoConfiguration#hunYuanChatClient(HulaAiProperties)}
     */
    private ChatModel buildHunYuanChatModel(String apiKey, String url) {
        HulaAiProperties.HunYuanProperties properties = new HulaAiProperties.HunYuanProperties();
        properties.setBaseUrl(url);
        properties.setApiKey(apiKey);
        return new AiAutoConfiguration().buildHunYuanChatClient(properties);
    }

    /**
     * 可参考 {@link AiAutoConfiguration#siliconFlowChatClient(HulaAiProperties)}
     */
    private ChatModel buildSiliconFlowChatModel(String apiKey) {
        HulaAiProperties.SiliconFlowProperties properties = new HulaAiProperties.SiliconFlowProperties();
        properties.setApiKey(apiKey);
        return new AiAutoConfiguration().buildSiliconFlowChatClient(properties);
    }

    /**
     * 可参考 {@link ZhiPuAiAutoConfiguration} 的 zhiPuAiChatModel 方法
     */
    private ZhiPuAiChatModel buildZhiPuChatModel(String apiKey, String url) {
        ZhiPuAiApi zhiPuAiApi = StrUtil.isEmpty(url) ? new ZhiPuAiApi(apiKey)
                : new ZhiPuAiApi(url, apiKey);
        ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder().model(ZhiPuAiApi.DEFAULT_CHAT_MODEL).temperature(0.7).build();
        return new ZhiPuAiChatModel(zhiPuAiApi, options, getFunctionCallbackResolver(), DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 可参考 {@link ZhiPuAiAutoConfiguration} 的 zhiPuAiImageModel 方法
     */
    private ZhiPuAiImageModel buildZhiPuAiImageModel(String apiKey, String url) {
        ZhiPuAiImageApi zhiPuAiApi = StrUtil.isEmpty(url) ? new ZhiPuAiImageApi(apiKey)
                : new ZhiPuAiImageApi(url, apiKey, RestClient.builder());
        return new ZhiPuAiImageModel(zhiPuAiApi);
    }

    /**
     * 可参考 {@link MiniMaxAutoConfiguration} 的 miniMaxChatModel 方法
     */
    private MiniMaxChatModel buildMiniMaxChatModel(String apiKey, String url) {
        MiniMaxApi miniMaxApi = StrUtil.isEmpty(url) ? new MiniMaxApi(apiKey)
                : new MiniMaxApi(url, apiKey);
        MiniMaxChatOptions options = MiniMaxChatOptions.builder().model(MiniMaxApi.DEFAULT_CHAT_MODEL).temperature(0.7).build();
        return new MiniMaxChatModel(miniMaxApi, options, getFunctionCallbackResolver(), DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 可参考 {@link MoonshotAutoConfiguration} 的 moonshotChatModel 方法
     */
    private MoonshotChatModel buildMoonshotChatModel(String apiKey, String url) {
        MoonshotApi moonshotApi = StrUtil.isEmpty(url)? new MoonshotApi(apiKey)
                : new MoonshotApi(url, apiKey);
        MoonshotChatOptions options = MoonshotChatOptions.builder().model(MoonshotApi.DEFAULT_CHAT_MODEL).build();
        return new MoonshotChatModel(moonshotApi, options, getFunctionCallbackResolver(), DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 可参考
     */
    private static XingHuoChatModel buildXingHuoChatModel(String key) {
        List<String> keys = StrUtil.split(key, '|');
        Assert.equals(keys.size(), 2, "XingHuoChatClient 的密钥需要 (appKey|secretKey) 格式");
        HulaAiProperties.XingHuoProperties properties = new HulaAiProperties.XingHuoProperties();
        properties.setAppKey(keys.get(0));
        properties.setSecretKey(keys.get(1));
        return new AiAutoConfiguration().buildXingHuoChatClient(properties);
    }

    /**
     * 可参考
     */
    private BaiChuanChatModel buildBaiChuanChatModel(String apiKey) {
        HulaAiProperties.BaiChuanProperties properties = new HulaAiProperties.BaiChuanProperties();
        properties.setApiKey(apiKey);
        return new AiAutoConfiguration().buildBaiChuanChatClient(properties);
    }

    /**
     * 可参考 {@link OpenAiAutoConfiguration} 的 openAiChatModel 方法
     */
    private static OpenAiChatModel buildOpenAiChatModel(String openAiToken, String url) {
        url = StrUtil.blankToDefault(url, OpenAiApiConstants.DEFAULT_BASE_URL);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(url).apiKey(openAiToken).build();
        return OpenAiChatModel.builder().openAiApi(openAiApi).toolCallingManager(getToolCallingManager()).build();
    }

    /**
     * 构建 Gitee AI 聊天模型（兼容 OpenAI 接口）
     */
    private static OpenAiChatModel buildGiteeAiChatModel(String apiKey) {
        // OpenAI 客户端会自动添加 /v1，所以这里只用 https://ai.gitee.com
        String baseUrl = "https://ai.gitee.com";
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(apiKey).build();
        return OpenAiChatModel.builder().openAiApi(openAiApi).toolCallingManager(getToolCallingManager()).build();
    }

    private ChatModel buildGeminiChatModel(String apiKey, String url) {
        String baseUrl = StrUtil.blankToDefault(url, "https://generativelanguage.googleapis.com/v1beta/openai");
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(apiKey).build();
        return OpenAiChatModel.builder().openAiApi(openAiApi).toolCallingManager(getToolCallingManager()).build();
    }

    /**
     * 构建 OpenRouter 聊天模型
     */
    private static OpenRouterChatModel buildOpenRouterChatModel(String apiKey) {
        HulaAiProperties.OpenRouterProperties properties = new HulaAiProperties.OpenRouterProperties();
        properties.setApiKey(apiKey);
        return new AiAutoConfiguration().buildOpenRouterChatClient(properties);
    }

    // TODO @芋艿：手头暂时没密钥，使用建议再测试下
    /**
     * 可参考 {@link AzureOpenAiAutoConfiguration}
     */
    private static AzureOpenAiChatModel buildAzureOpenAiChatModel(String apiKey, String url) {
        AzureOpenAiAutoConfiguration azureOpenAiAutoConfiguration = new AzureOpenAiAutoConfiguration();
        // 创建 OpenAIClient 对象
        AzureOpenAiConnectionProperties connectionProperties = new AzureOpenAiConnectionProperties();
        connectionProperties.setApiKey(apiKey);
        connectionProperties.setEndpoint(url);
        OpenAIClientBuilder openAIClient = azureOpenAiAutoConfiguration.openAIClientBuilder(connectionProperties, null);
        // 获取 AzureOpenAiChatProperties 对象
        AzureOpenAiChatProperties chatProperties = SpringUtil.getBean(AzureOpenAiChatProperties.class);
        return azureOpenAiAutoConfiguration.azureOpenAiChatModel(openAIClient, chatProperties,
                getToolCallingManager(), null, null);
    }

    /**
     * 可参考 {@link OpenAiAutoConfiguration} 的 openAiImageModel 方法
     */
    private OpenAiImageModel buildOpenAiImageModel(String openAiToken, String url) {
        url = StrUtil.blankToDefault(url, OpenAiApiConstants.DEFAULT_BASE_URL);
        OpenAiImageApi openAiApi = OpenAiImageApi.builder().baseUrl(url).apiKey(openAiToken).build();
        return new OpenAiImageModel(openAiApi);
    }

    /**
     * 构建 Gitee AI 图片模型（兼容 OpenAI 接口）
     */
    private OpenAiImageModel buildGiteeAiImageModel(String apiKey) {
        // OpenAI 客户端会自动添加 /v1，所以这里只用 https://ai.gitee.com
        String baseUrl = "https://ai.gitee.com";
        OpenAiImageApi openAiApi = OpenAiImageApi.builder().baseUrl(baseUrl).apiKey(apiKey).build();
        return new OpenAiImageModel(openAiApi);
    }

    /**
     * 创建 SiliconFlowImageModel 对象
     */
    private SiliconFlowImageModel buildSiliconFlowImageModel(String apiToken, String url) {
        url = StrUtil.blankToDefault(url, SiliconFlowApiConstants.DEFAULT_BASE_URL);
        SiliconFlowImageApi openAiApi = new SiliconFlowImageApi(url, apiToken);
        return new SiliconFlowImageModel(openAiApi);
    }

    /**
     * 可参考 {@link OllamaAutoConfiguration} 的 ollamaApi 方法
     */
    private static OllamaChatModel buildOllamaChatModel(String url) {
        OllamaApi ollamaApi = new OllamaApi(url);
        return OllamaChatModel.builder().ollamaApi(ollamaApi).toolCallingManager(getToolCallingManager()).build();
    }

    /**
     * 可参考 {@link StabilityAiImageAutoConfiguration} 的 stabilityAiImageModel 方法
     */
    private StabilityAiImageModel buildStabilityAiImageModel(String apiKey, String url) {
        url = StrUtil.blankToDefault(url, StabilityAiApi.DEFAULT_BASE_URL);
        StabilityAiApi stabilityAiApi = new StabilityAiApi(apiKey, StabilityAiApi.DEFAULT_IMAGE_MODEL, url);
        return new StabilityAiImageModel(stabilityAiApi);
    }

    // ========== 各种创建 EmbeddingModel 的方法 ==========

    /**
     * 可参考 {@link DashScopeAutoConfiguration} 的 dashscopeEmbeddingModel 方法
     */
    private DashScopeEmbeddingModel buildTongYiEmbeddingModel(String apiKey, String model) {
        DashScopeApi dashScopeApi = new DashScopeApi(apiKey);
        DashScopeEmbeddingOptions dashScopeEmbeddingOptions = DashScopeEmbeddingOptions.builder().withModel(model).build();
        return new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED, dashScopeEmbeddingOptions);
    }

    /**
     * 可参考 {@link ZhiPuAiAutoConfiguration} 的 zhiPuAiEmbeddingModel 方法
     */
    private ZhiPuAiEmbeddingModel buildZhiPuEmbeddingModel(String apiKey, String url, String model) {
        ZhiPuAiApi zhiPuAiApi = StrUtil.isEmpty(url) ? new ZhiPuAiApi(apiKey)
                : new ZhiPuAiApi(url, apiKey);
        ZhiPuAiEmbeddingOptions zhiPuAiEmbeddingOptions = ZhiPuAiEmbeddingOptions.builder().model(model).build();
        return new ZhiPuAiEmbeddingModel(zhiPuAiApi, MetadataMode.EMBED, zhiPuAiEmbeddingOptions);
    }

    /**
     * 可参考 {@link MiniMaxAutoConfiguration} 的 miniMaxEmbeddingModel 方法
     */
    private EmbeddingModel buildMiniMaxEmbeddingModel(String apiKey, String url, String model) {
        MiniMaxApi miniMaxApi = StrUtil.isEmpty(url)? new MiniMaxApi(apiKey)
                : new MiniMaxApi(url, apiKey);
        MiniMaxEmbeddingOptions miniMaxEmbeddingOptions = MiniMaxEmbeddingOptions.builder().model(model).build();
        return new MiniMaxEmbeddingModel(miniMaxApi, MetadataMode.EMBED, miniMaxEmbeddingOptions);
    }

    /**
     * 可参考 {@link QianFanAutoConfiguration} 的 qianFanEmbeddingModel 方法
     */
    private QianFanEmbeddingModel buildYiYanEmbeddingModel(String key, String model) {
        List<String> keys = StrUtil.split(key, '|');
        Assert.equals(keys.size(), 2, "YiYanChatClient 的密钥需要 (appKey|secretKey) 格式");
        String appKey = keys.get(0);
        String secretKey = keys.get(1);
        QianFanApi qianFanApi = new QianFanApi(appKey, secretKey);
        QianFanEmbeddingOptions qianFanEmbeddingOptions = QianFanEmbeddingOptions.builder().model(model).build();
        return new QianFanEmbeddingModel(qianFanApi, MetadataMode.EMBED, qianFanEmbeddingOptions);
    }

    private OllamaEmbeddingModel buildOllamaEmbeddingModel(String url, String model) {
        OllamaApi ollamaApi = new OllamaApi(url);
        OllamaOptions ollamaOptions = OllamaOptions.builder().model(model).build();
        return OllamaEmbeddingModel.builder().ollamaApi(ollamaApi).defaultOptions(ollamaOptions).build();
    }

    /**
     * 可参考 {@link OpenAiAutoConfiguration} 的 openAiEmbeddingModel 方法
     */
    private OpenAiEmbeddingModel buildOpenAiEmbeddingModel(String openAiToken, String url, String model) {
        url = StrUtil.blankToDefault(url, OpenAiApiConstants.DEFAULT_BASE_URL);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(url).apiKey(openAiToken).build();
        OpenAiEmbeddingOptions openAiEmbeddingProperties = OpenAiEmbeddingOptions.builder().model(model).build();
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, openAiEmbeddingProperties);
    }

    // TODO @芋艿：手头暂时没密钥，使用建议再测试下
    /**
     * 可参考 {@link AzureOpenAiAutoConfiguration} 的 azureOpenAiEmbeddingModel 方法
     */
    private AzureOpenAiEmbeddingModel buildAzureOpenAiEmbeddingModel(String apiKey, String url, String model) {
        AzureOpenAiAutoConfiguration azureOpenAiAutoConfiguration = new AzureOpenAiAutoConfiguration();
        // 创建 OpenAIClient 对象
        AzureOpenAiConnectionProperties connectionProperties = new AzureOpenAiConnectionProperties();
        connectionProperties.setApiKey(apiKey);
        connectionProperties.setEndpoint(url);
        OpenAIClientBuilder openAIClient = azureOpenAiAutoConfiguration.openAIClientBuilder(connectionProperties, null);
        // 获取 AzureOpenAiChatProperties 对象
        AzureOpenAiEmbeddingProperties embeddingProperties = SpringUtil.getBean(AzureOpenAiEmbeddingProperties.class);
        return azureOpenAiAutoConfiguration.azureOpenAiEmbeddingModel(openAIClient, embeddingProperties,
                null, null);
    }

    // ========== 各种创建 VectorStore 的方法 ==========

    /**
     * 注意：仅适合本地测试使用，生产建议还是使用 Qdrant、Milvus 等
     */
    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private SimpleVectorStore buildSimpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        // 启动加载
        File file = new File(StrUtil.format("{}/vector_store/simple_{}.json",
                FileUtil.getUserHomePath(), embeddingModel.getClass().getSimpleName()));
        if (!file.exists()) {
            FileUtil.mkParentDirs(file);
            file.createNewFile();
        } else if (file.length() > 0) {
            vectorStore.load(file);
        }
        // 定时持久化，每分钟一次
        Timer timer = new Timer("SimpleVectorStoreTimer-" + file.getAbsolutePath());
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                vectorStore.save(file);
            }

        }, Duration.ofMinutes(1).toMillis(), Duration.ofMinutes(1).toMillis());
        // 关闭时，进行持久化
        RuntimeUtil.addShutdownHook(() -> vectorStore.save(file));
        return vectorStore;
    }

    /**
     * 参考 {@link QdrantVectorStoreAutoConfiguration} 的 vectorStore 方法
     */
    @SneakyThrows
    private QdrantVectorStore buildQdrantVectorStore(EmbeddingModel embeddingModel) {
        QdrantVectorStoreAutoConfiguration configuration = new QdrantVectorStoreAutoConfiguration();
        QdrantVectorStoreProperties properties = SpringUtil.getBean(QdrantVectorStoreProperties.class);
        // 参考 QdrantVectorStoreAutoConfiguration 实现，创建 QdrantClient 对象
        QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(
                properties.getHost(), properties.getPort(), properties.isUseTls());
        if (StrUtil.isNotEmpty(properties.getApiKey())) {
            grpcClientBuilder.withApiKey(properties.getApiKey());
        }
        QdrantClient qdrantClient = new QdrantClient(grpcClientBuilder.build());
        // 创建 QdrantVectorStore 对象
        QdrantVectorStore vectorStore = configuration.vectorStore(embeddingModel, properties, qdrantClient,
                getObservationRegistry(), getCustomObservationConvention(), getBatchingStrategy());
        // 初始化索引
        vectorStore.afterPropertiesSet();
        return vectorStore;
    }

    /**
     * 参考 {@link RedisVectorStoreAutoConfiguration} 的 vectorStore 方法
     */
    private RedisVectorStore buildRedisVectorStore(EmbeddingModel embeddingModel,
                                                   Map<String, Class<?>> metadataFields) {
        // 创建 JedisPooled 对象
        RedisProperties redisProperties = SpringUtil.getBean(RedisProperties.class);
        JedisPooled jedisPooled = new JedisPooled(redisProperties.getHost(), redisProperties.getPort());
        // 创建 RedisVectorStoreProperties 对象
        RedisVectorStoreAutoConfiguration configuration = new RedisVectorStoreAutoConfiguration();
        RedisVectorStoreProperties properties = SpringUtil.getBean(RedisVectorStoreProperties.class);
        RedisVectorStore redisVectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(properties.getIndex()).prefix(properties.getPrefix())
                .initializeSchema(properties.isInitializeSchema())
                .metadataFields(CollectionUtils.convertList(metadataFields.entrySet(), entry -> {
                    String fieldName = entry.getKey();
                    Class<?> fieldType = entry.getValue();
                    if (Number.class.isAssignableFrom(fieldType)) {
                        return RedisVectorStore.MetadataField.numeric(fieldName);
                    }
                    if (Boolean.class.isAssignableFrom(fieldType)) {
                        return RedisVectorStore.MetadataField.tag(fieldName);
                    }
                    return RedisVectorStore.MetadataField.text(fieldName);
                }))
                .observationRegistry(getObservationRegistry().getObject())
                .customObservationConvention(getCustomObservationConvention().getObject())
                .batchingStrategy(getBatchingStrategy())
                .build();
        // 初始化索引
        redisVectorStore.afterPropertiesSet();
        return redisVectorStore;
    }

    /**
     * 参考 {@link MilvusVectorStoreAutoConfiguration} 的 vectorStore 方法
     */
    @SneakyThrows
    private MilvusVectorStore buildMilvusVectorStore(EmbeddingModel embeddingModel) {
        MilvusVectorStoreAutoConfiguration configuration = new MilvusVectorStoreAutoConfiguration();
        // 获取配置属性
        MilvusVectorStoreProperties serverProperties = SpringUtil.getBean(MilvusVectorStoreProperties.class);
        MilvusServiceClientProperties clientProperties = SpringUtil.getBean(MilvusServiceClientProperties.class);

        // 创建 MilvusServiceClient 对象
        MilvusServiceClient milvusClient = configuration.milvusClient(serverProperties, clientProperties,
                new MilvusServiceClientConnectionDetails() {

                    @Override
                    public String getHost() {
                        return clientProperties.getHost();
                    }

                    @Override
                    public int getPort() {
                        return clientProperties.getPort();
                    }

                }
        );
        // 创建 MilvusVectorStore 对象
        MilvusVectorStore vectorStore = configuration.vectorStore(milvusClient, embeddingModel, serverProperties,
                getBatchingStrategy(), getObservationRegistry(), getCustomObservationConvention());

        // 初始化索引
        vectorStore.afterPropertiesSet();
        return vectorStore;
    }

    private static ObjectProvider<ObservationRegistry> getObservationRegistry() {
        return new ObjectProvider<>() {

            @Override
            public ObservationRegistry getObject() throws BeansException {
                return SpringUtil.getBean(ObservationRegistry.class);
            }

        };
    }

    private static ObjectProvider<VectorStoreObservationConvention> getCustomObservationConvention() {
        return new ObjectProvider<>() {
            @Override
            public VectorStoreObservationConvention getObject() throws BeansException {
                return new DefaultVectorStoreObservationConvention();
            }
        };
    }

    private static BatchingStrategy getBatchingStrategy() {
        return SpringUtil.getBean(BatchingStrategy.class);
    }

    private static ToolCallingManager getToolCallingManager() {
        return SpringUtil.getBean(ToolCallingManager.class);
    }

    private static FunctionCallbackResolver getFunctionCallbackResolver() {
        return SpringUtil.getBean(FunctionCallbackResolver.class);
    }

    // ========== VideoModel 相关方法 ==========

    @Override
    public VideoModel getOrCreateVideoModel(AiPlatformEnum platform, String apiKey, String url) {
        String cacheKey = buildClientCacheKey(VideoModel.class, platform, apiKey, url);
        return Singleton.get(cacheKey, (Func0<VideoModel>) () -> {
            switch (platform) {
                case SILICON_FLOW:
                    return buildSiliconFlowVideoModel(apiKey);
                case GITEE_AI:
                    return buildGiteeAiVideoModel(apiKey);
                // TODO: 添加其他平台支持
                // case DEEP_SEEK:
                //     return buildDeepSeekVideoModel(apiKey, url);
                // case KIMI:
                //     return buildKimiVideoModel(apiKey, url);
                default:
                    throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
            }
        });
    }

    private VideoModel buildSiliconFlowVideoModel(String apiKey) {
        SiliconFlowVideoApi videoApi = new SiliconFlowVideoApi(apiKey);
        return new SiliconFlowVideoModel(videoApi);
    }

    /**
     * 构建 Gitee AI 视频模型
     */
    private VideoModel buildGiteeAiVideoModel(String apiKey) {
        GiteeAiVideoApi videoApi = new GiteeAiVideoApi(apiKey);
        return new GiteeAiVideoModel(videoApi);
    }

    // ========== AudioModel 相关方法 ==========

    @Override
    public AudioModel getOrCreateAudioModel(AiPlatformEnum platform, String apiKey, String url) {
        String cacheKey = buildClientCacheKey(AudioModel.class, platform, apiKey, url);
        return Singleton.get(cacheKey, (Func0<AudioModel>) () -> {
            switch (platform) {
                case SILICON_FLOW:
                    return buildSiliconFlowAudioModel(apiKey);
                case GITEE_AI:
                    return buildGiteeAiAudioModel(apiKey);
                // TODO: 添加其他平台支持
                // case DEEP_SEEK:
                //     return buildDeepSeekAudioModel(apiKey, url);
                // case KIMI:
                //     return buildKimiAudioModel(apiKey, url);
                default:
                    throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
            }
        });
    }

    private AudioModel buildSiliconFlowAudioModel(String apiKey) {
        SiliconFlowAudioApi audioApi = new SiliconFlowAudioApi(apiKey);
        return new SiliconFlowAudioModel(audioApi);
    }

    private AudioModel buildGiteeAiAudioModel(String apiKey) {
        // 使用 GiteeAiAudioApi 构建 API 客户端
        GiteeAiAudioApi audioApi = new GiteeAiAudioApi(apiKey);
        return new GiteeAiAudioModel(audioApi);
    }
}
