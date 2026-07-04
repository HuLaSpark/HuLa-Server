package com.luohuo.flex.ai;

import com.luohuo.basic.validator.annotation.EnableFormValidator;
import com.luohuo.flex.common.ServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.net.UnknownHostException;

import static com.luohuo.flex.common.constant.BizConstant.BUSINESS_PACKAGE;
import static com.luohuo.flex.common.constant.BizConstant.UTIL_PACKAGE;

@SpringBootApplication(excludeName = {
        "com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAutoConfiguration",
        "org.springframework.ai.autoconfigure.anthropic.AnthropicAutoConfiguration",
        "org.springframework.ai.autoconfigure.minimax.MiniMaxAutoConfiguration",
        "org.springframework.ai.autoconfigure.mistralai.MistralAiAutoConfiguration",
        "org.springframework.ai.autoconfigure.moonshot.MoonshotAutoConfiguration",
        "org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration",
        "org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration",
        "org.springframework.ai.autoconfigure.qianfan.QianFanAutoConfiguration",
        "org.springframework.ai.autoconfigure.stabilityai.StabilityAiImageAutoConfiguration",
        "org.springframework.ai.autoconfigure.vectorstore.elasticsearch.ElasticsearchVectorStoreAutoConfiguration",
        "org.springframework.ai.autoconfigure.watsonxai.WatsonxAiAutoConfiguration",
        "org.springframework.ai.autoconfigure.zhipuai.ZhiPuAiAutoConfiguration"
})
@EnableDiscoveryClient
@Configuration
@ComponentScan({
        UTIL_PACKAGE, BUSINESS_PACKAGE
})
@EnableFeignClients(value = {
        UTIL_PACKAGE, BUSINESS_PACKAGE
})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@EnableFormValidator
public class AiServerApplication extends ServerApplication {
    public static void main(String[] args) throws UnknownHostException {
        start(AiServerApplication.class, args);
    }
}
