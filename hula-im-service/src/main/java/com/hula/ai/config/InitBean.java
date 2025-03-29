package com.hula.ai.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.common.constant.AiConstants;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.common.enums.IntegerEnum;
import com.hula.ai.config.dto.BaseInfoDTO;
import com.hula.ai.gpt.pojo.vo.OpenkeyVO;
import com.hula.ai.gpt.service.IOpenkeyService;
import com.hula.ai.llm.chatglm.ChatGLMClient;
import com.hula.ai.llm.deepseek.DeepSeekStreamClient;
import com.hula.ai.llm.deepseek.constant.DeepSeekConst;
import com.hula.ai.llm.doubao.DouBaoClient;
import com.hula.ai.llm.internlm.InternlmClient;
import com.hula.ai.llm.locallm.LocalLMClient;
import com.hula.ai.llm.moonshot.MoonshotClient;
import com.hula.ai.llm.openai.OpenAiClient;
import com.hula.ai.llm.openai.OpenAiStreamClient;
import com.hula.ai.llm.openai.function.KeyRandomStrategy;
import com.hula.ai.llm.openai.interceptor.OpenAILogger;
import com.hula.ai.llm.spark.SparkClient;
import com.hula.ai.llm.tongyi.TongYiClient;
import com.hula.ai.llm.wenxin.WenXinClient;
import com.hula.core.user.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 初始化大模型bean
 *
 * @author: 云裂痕
 * @date: 2025/03/07
 * 得其道 乾乾
 */
@Slf4j
@Configuration
public class InitBean {
    @Autowired
    private IOpenkeyService openkeyService;
    @Autowired
    private ConfigService configService;

    @Bean
    public OpenAiStreamClient openAiStreamClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.OPENAI.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到ChatGpt模型token数据");
            return new OpenAiStreamClient();
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                // 如使用代理 请更换为代理地址
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080)))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        BaseInfoDTO baseInfo = configService.getBeanByName(AiConstants.BASE_INFO, BaseInfoDTO.class);
        String apiHost = null;
        if (baseInfo.getProxyType().equals(IntegerEnum.THREE.getValue()) && StrUtil.isEmpty((baseInfo.getProxyAddress()))) {
            if (!baseInfo.getProxyAddress().contains(StringPoolConstant.COLON)) {
                log.error("代理地址错误");
                return new OpenAiStreamClient();
            }
            String[] proxyAddress = baseInfo.getProxyAddress().split(StringPoolConstant.COLON);
            okHttpClient = new OkHttpClient
                    .Builder()
                    // 如使用代理 请更换为代理地址
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress[0], Integer.valueOf(proxyAddress[1]))))
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .readTimeout(600, TimeUnit.SECONDS)
                    .build();
        } else if (baseInfo.getProxyType().equals(IntegerEnum.TWO.getValue()) && StrUtil.isEmpty((baseInfo.getProxyServer()))) {
            apiHost = baseInfo.getProxyServer();
        }
        return OpenAiStreamClient
                .builder()
                .apiHost(apiHost)
                .apiKey(openkeys.stream().map(v -> v.getAppKey()).collect(Collectors.toList()))
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
    }

    @Bean
    public OpenAiClient openAiClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.MOONSHOT.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到ChatGpt模型token数据");
            return new OpenAiClient();
        }
        //本地开发需要配置代理地址
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 如使用代理 请更换为代理地址
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080)))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();

        BaseInfoDTO baseInfo = configService.getBeanByName(AiConstants.BASE_INFO, BaseInfoDTO.class);
		String apiHost = null;
        if (baseInfo.getProxyType().equals(IntegerEnum.THREE.getValue()) && StrUtil.isEmpty((baseInfo.getProxyAddress()))) {
            if (!baseInfo.getProxyAddress().contains(StringPoolConstant.COLON)) {
                log.error("代理地址错误");
                return new OpenAiClient();
            }
            String[] proxyAddress = baseInfo.getProxyAddress().split(StringPoolConstant.COLON);
            okHttpClient = new OkHttpClient
                    .Builder()
                    // 如使用代理 请更换为代理地址
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress[0], Integer.valueOf(proxyAddress[1]))))
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .readTimeout(600, TimeUnit.SECONDS)
                    .build();
        } else if (baseInfo.getProxyType().equals(IntegerEnum.TWO.getValue()) && StrUtil.isNotEmpty(baseInfo.getProxyServer())) {
            apiHost = baseInfo.getProxyServer();
        }
        return OpenAiClient
                .builder()
                .apiHost(apiHost)
                .apiKey(openkeys.stream().map(v -> v.getAppKey()).collect(Collectors.toList()))
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
    }

    /**
     * 文心一言
     *
     * @return
     */
    @Bean
    public WenXinClient wenXinClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.WENXIN.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到文心一言模型token数据");
            return new WenXinClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        if (StrUtil.isEmpty(openkey.getAppKey()) || StrUtil.isEmpty(openkey.getAppSecret())) {
            log.error("未获取到文心一言模型token数据");
            return new WenXinClient();
        }
        return WenXinClient.builder().logLevel(HttpLoggingInterceptor.Level.BASIC)
                .apiKey(openkey.getAppKey()).secretKey(openkey.getAppSecret()).build();
    }

    /**
     * 通义千问
     *
     * @return
     */
    @Bean
    public TongYiClient tongYiClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.TONGYI.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到通义千问模型token数据");
            return new TongYiClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        return new TongYiClient(openkey.getAppKey());
    }

    /**
     * 讯飞星火
     *
     * @return
     */
    @Bean
    public SparkClient sparkClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.SPARK.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到智谱清言模型token数据，请添加后需要重启系统");
            return new SparkClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        return new SparkClient(openkey.getAppId(), openkey.getAppKey(), openkey.getAppSecret());
    }

    /**
     * 智谱清言
     *
     * @return
     */
    @Bean
    public ChatGLMClient chatGLMClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.CHATGLM.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到智谱清言模型token数据，请添加后需要重启系统");
            return new ChatGLMClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        if (StrUtil.isEmpty(openkey.getAppKey())) {
            log.error("未获取到智谱清言模型token数据");
            return new ChatGLMClient();
        }
        return ChatGLMClient.builder().appKey(openkey.getAppKey()).appSecret(openkey.getAppSecret()).apiSecretKey(openkey.getAppKey()).build();
    }

    /**
     * 月之暗面
     *
     * @return
     */
    @Bean
    public MoonshotClient moonshotClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.MOONSHOT.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到月之暗面模型token数据，请添加后需要重启系统");
            return new MoonshotClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        if (StrUtil.isEmpty((openkey.getAppKey()))) {
            log.error("未获取到月之暗面模型token数据");
            return new MoonshotClient();
        }
        return MoonshotClient.builder().apiKey(openkey.getAppKey()).build();
    }

    /**
     * DeepSeek
     * @return
     */
    @Bean
    public DeepSeekStreamClient deepSeekStreamClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.DEEPSEEK.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到DeepSeek模型token数据");
            return new DeepSeekStreamClient();
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                // 如使用代理 请更换为代理地址
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080)))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        return DeepSeekStreamClient
                .builder()
                .apiHost(DeepSeekConst.HOST)
                .apiKey(openkeys.stream().map(v -> v.getAppKey()).collect(Collectors.toList()))
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
    }

    /**
     * 豆包
     *
     * @return
     */
    @Bean
    public DouBaoClient douBaoClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.DOUBAO.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到豆包模型token数据，请添加后需要重启系统");
            return new DouBaoClient();
        }
        if (CollUtil.isNotEmpty(openkeys)) {
            return DouBaoClient.builder().apiKey(openkeys.get(0).getAppKey()).build();
        }
        return DouBaoClient.builder().build();
    }

    /**
     * 书生·浦语
     *
     * @return
     */
    @Bean
    public InternlmClient internlmClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.INTERNLM.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            log.error("未加载到书生浦语模型token数据，请添加后需要重启系统");
            return new InternlmClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        if (StrUtil.isEmpty((openkey.getAppKey()))) {
            log.error("未获取到书生浦语模型token数据");
            return new InternlmClient();
        }
        return InternlmClient.builder().token(openkey.getAppKey()).build();
    }

    /**
     * LocalLM 本地模型
     * 支持Langchain-Chatchat、Ollama、GiteeAI、扣子、FastGPT、LinkAI、Dify
     *
     * @return
     */
    @Bean
    public LocalLMClient localLMClient() {
        List<OpenkeyVO> openkeys = openkeyService.listOpenkeyByModel(ChatModelEnum.LOCALLM.getValue());
        if (CollUtil.isEmpty(openkeys)) {
            return LocalLMClient.builder().apiKey(openkeys.get(0).getAppKey()).build();
        }
        return LocalLMClient.builder().build();
    }

}
