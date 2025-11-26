package com.luohuo.flex.ai.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.luohuo.basic.jackson.LuohuoJacksonModule;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.flex.ai.core.AiModelFactory;
import com.luohuo.flex.ai.core.AiModelFactoryImpl;
import com.luohuo.flex.ai.core.model.BaiChuanChatModel;
import com.luohuo.flex.ai.core.model.DeepSeekChatModel;
import com.luohuo.flex.ai.core.model.DouBaoChatModel;
import com.luohuo.flex.ai.core.model.HunYuanChatModel;
import com.luohuo.flex.ai.core.model.MidjourneyApi;
import com.luohuo.flex.ai.core.model.SunoApi;
import com.luohuo.flex.ai.core.model.XingHuoChatModel;
import com.luohuo.flex.ai.core.model.google.GeminiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import com.luohuo.flex.ai.core.model.openrouter.OpenRouterApiConstants;
import com.luohuo.flex.ai.core.model.openrouter.OpenRouterChatModel;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowApiConstants;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.autoconfigure.vectorstore.qdrant.QdrantVectorStoreProperties;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

@Configuration
@EnableConfigurationProperties({HulaAiProperties.class,
        QdrantVectorStoreProperties.class, // 解析 Qdrant 配置
        RedisVectorStoreProperties.class, // 解析 Redis 配置
        MilvusVectorStoreProperties.class,
        MilvusServiceClientProperties.class // 解析 Milvus 配置
})
@EnableAsync
@Slf4j
public class AiAutoConfiguration {
    @Bean
    public AiModelFactory aiModelFactory() {
        return new AiModelFactoryImpl();
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.deepseek.enable", havingValue = "true")
    public DeepSeekChatModel deepSeekChatModel(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.DeepSeekProperties properties = hulaAiProperties.getDeepseek();
        return buildDeepSeekChatModel(properties);
    }

    public DeepSeekChatModel buildDeepSeekChatModel(HulaAiProperties.DeepSeekProperties properties) {
        if (StrUtil.isEmpty(properties.getModel())) {
            properties.setModel(DeepSeekChatModel.MODEL_DEFAULT);
        }
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(DeepSeekChatModel.BASE_URL)
                        .apiKey(properties.getApiKey())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        return new DeepSeekChatModel(openAiChatModel);
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.doubao.enable", havingValue = "true")
    public DouBaoChatModel douBaoChatClient(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.DouBaoProperties properties = hulaAiProperties.getDoubao();
        return buildDouBaoChatClient(properties);
    }

    public DouBaoChatModel buildDouBaoChatClient(HulaAiProperties.DouBaoProperties properties) {
        if (StrUtil.isEmpty(properties.getModel())) {
            properties.setModel(DouBaoChatModel.MODEL_DEFAULT);
        }
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(DouBaoChatModel.BASE_URL)
                        .apiKey(properties.getApiKey())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        return new DouBaoChatModel(openAiChatModel);
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.siliconflow.enable", havingValue = "true")
    public SiliconFlowChatModel siliconFlowChatClient(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.SiliconFlowProperties properties = hulaAiProperties.getSiliconflow();
        return buildSiliconFlowChatClient(properties);
    }

    public SiliconFlowChatModel buildSiliconFlowChatClient(HulaAiProperties.SiliconFlowProperties properties) {
        if (StrUtil.isEmpty(properties.getModel())) {
            properties.setModel(SiliconFlowApiConstants.MODEL_DEFAULT);
        }
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(SiliconFlowApiConstants.DEFAULT_BASE_URL)
                        .apiKey(properties.getApiKey())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        TokenCountEstimator tokenCountEstimator = new JTokkitTokenCountEstimator();
        return new SiliconFlowChatModel(openAiChatModel, tokenCountEstimator);
    }

    @Bean("videoTaskExecutor")
    public ThreadPoolTaskExecutor videoTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("video-exec-");
        executor.initialize();
        return executor;
    }

    @Bean("videoTaskScheduler")
    public ThreadPoolTaskScheduler videoTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("video-sched-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.hunyuan.enable", havingValue = "true")
    public HunYuanChatModel hunYuanChatClient(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.HunYuanProperties properties = hulaAiProperties.getHunyuan();
        return buildHunYuanChatClient(properties);
    }

    public HunYuanChatModel buildHunYuanChatClient(HulaAiProperties.HunYuanProperties properties) {
        if (StrUtil.isEmpty(properties.getModel())) {
            properties.setModel(HunYuanChatModel.MODEL_DEFAULT);
        }
        // 特殊：由于混元大模型不提供 deepseek，而是通过知识引擎，所以需要区分下 URL
        if (StrUtil.isEmpty(properties.getBaseUrl())) {
            properties.setBaseUrl(
                    StrUtil.startWithIgnoreCase(properties.getModel(), "deepseek") ? HunYuanChatModel.DEEP_SEEK_BASE_URL
                            : HunYuanChatModel.BASE_URL);
        }
        // 创建 OpenAiChatModel、HunYuanChatModel 对象
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(properties.getBaseUrl())
                        .apiKey(properties.getApiKey())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        return new HunYuanChatModel(openAiChatModel);
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.xinghuo.enable", havingValue = "true")
    public XingHuoChatModel xingHuoChatClient(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.XingHuoProperties properties = hulaAiProperties.getXinghuo();
        return buildXingHuoChatClient(properties);
    }

    public XingHuoChatModel buildXingHuoChatClient(HulaAiProperties.XingHuoProperties properties) {
        if (StrUtil.isEmpty(properties.getModel())) {
            properties.setModel(XingHuoChatModel.MODEL_DEFAULT);
        }
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(XingHuoChatModel.BASE_URL)
                        .apiKey(properties.getAppKey() + ":" + properties.getSecretKey())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        return new XingHuoChatModel(openAiChatModel);
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.baichuan.enable", havingValue = "true")
    public BaiChuanChatModel baiChuanChatClient(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.BaiChuanProperties properties = hulaAiProperties.getBaichuan();
        return buildBaiChuanChatClient(properties);
    }

    public BaiChuanChatModel buildBaiChuanChatClient(HulaAiProperties.BaiChuanProperties properties) {
        if (StrUtil.isEmpty(properties.getModel())) {
            properties.setModel(BaiChuanChatModel.MODEL_DEFAULT);
        }
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(BaiChuanChatModel.BASE_URL)
                        .apiKey(properties.getApiKey())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        return new BaiChuanChatModel(openAiChatModel);
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.openrouter.enable", havingValue = "true")
    public OpenRouterChatModel openRouterChatClient(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.OpenRouterProperties properties = hulaAiProperties.getOpenrouter();
        return buildOpenRouterChatClient(properties);
    }

    public OpenRouterChatModel buildOpenRouterChatClient(HulaAiProperties.OpenRouterProperties properties) {
        if (StrUtil.isEmpty(properties.getModel())) {
            properties.setModel(OpenRouterApiConstants.MODEL_DEFAULT);
        }
        // 支持自定义 Base URL（可用于代理）
        String baseUrl = StrUtil.isNotEmpty(properties.getBaseUrl())
                ? properties.getBaseUrl()
                : OpenRouterApiConstants.DEFAULT_BASE_URL;

        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(baseUrl)
                        .apiKey(properties.getApiKey())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        return new OpenRouterChatModel(openAiChatModel);
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.midjourney.enable", havingValue = "true")
    public MidjourneyApi midjourneyApi(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.MidjourneyProperties config = hulaAiProperties.getMidjourney();
        return new MidjourneyApi(config.getBaseUrl(), config.getApiKey(), config.getNotifyUrl());
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.suno.enable", havingValue = "true")
    public SunoApi sunoApi(HulaAiProperties hulaAiProperties) {
        return new SunoApi(hulaAiProperties.getSuno().getBaseUrl());
    }

    @Bean
    @ConditionalOnProperty(value = "luohuo.ai.gemini.enable", havingValue = "true")
    public GeminiChatModel geminiChatClient(HulaAiProperties hulaAiProperties) {
        HulaAiProperties.GeminiProperties properties = hulaAiProperties.getGemini();
        String baseUrl = StrUtil.isNotEmpty(properties.getBaseUrl()) ? properties.getBaseUrl() : "https://generativelanguage.googleapis.com/v1beta/openai";
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder().baseUrl(baseUrl).apiKey(properties.getApiKey()).build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(properties.getModel())
                        .temperature(properties.getTemperature())
                        .maxTokens(properties.getMaxTokens())
                        .topP(properties.getTopP())
                        .build())
                .toolCallingManager(getToolCallingManager())
                .build();
        return new GeminiChatModel(openAiChatModel);
    }

    // ========== RAG 相关 ==========

    @Bean
    public TokenCountEstimator tokenCountEstimator() {
        return new JTokkitTokenCountEstimator();
    }

    @Bean
    public BatchingStrategy batchingStrategy() {
        return new TokenCountBatchingStrategy();
    }

    private static ToolCallingManager getToolCallingManager() {
        return SpringUtil.getBean(ToolCallingManager.class);
    }

	/**
	 * Spring 工具类
	 *
	 * @param applicationContext 上下文
	 */
	@Bean
	public SpringUtils getSpringUtils(ApplicationContext applicationContext) {
		SpringUtils instance = SpringUtils.getInstance();
		SpringUtils.setApplicationContext(applicationContext);
		return instance;
	}

	@Bean
	@Primary
	@ConditionalOnClass(ObjectMapper.class)
	@ConditionalOnMissingBean
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		objectMapper
				.setLocale(Locale.CHINA)
				//去掉默认的时间戳格式
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				// 时区
				.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
				//Date参数日期格式
				.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.CHINA))

				//该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
				.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
				// 忽略不能转义的字符
				.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
				//在使用spring boot + jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				//忽略未知字段
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				//单引号处理
				.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 注册自定义模块
		objectMapper.registerModule(new LuohuoJacksonModule()).findAndRegisterModules();

		return objectMapper;
	}
}
