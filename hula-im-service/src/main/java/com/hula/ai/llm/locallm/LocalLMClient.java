package com.hula.ai.llm.locallm;

import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hula.ai.common.constant.AuthConstant;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.locallm.base.BaseChatCompletion;
import com.hula.ai.llm.locallm.interceptor.LocalLMInterceptor;
import com.hula.ai.llm.locallm.interceptor.LocalLMLogger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * LangChain-Chatchat client
 *
 * @author: 云裂痕
 * @date: 2023/12/4
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@NoArgsConstructor(force = true)
@Slf4j
public class LocalLMClient {

    @NotNull
    @Getter
    @Setter
    private String accessToken;

    @NotNull
    @Getter
    @Setter
    private String apiKey;

    @Getter
    private OkHttpClient okHttpClient;

    private LocalLMClient(Builder builder) {
        accessToken = builder.accessToken;
        apiKey = builder.apiKey;
        if (Objects.isNull(builder.okHttpClient)) {
            log.info("提示：禁止在生产环境使用BODY级别日志，可以用：NONE,BASIC,HEADERS");
            if (Objects.isNull(builder.logLevel)) {
                builder.logLevel = HttpLoggingInterceptor.Level.HEADERS;
            }
            builder.okHttpClient = this.okHttpClient(builder.logLevel);
        }
        okHttpClient = builder.okHttpClient;
    }

    /**
     * 构造
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * langchain使用
     * 流式响应
     *
     * @param
     * @param eventSourceListener
     * @param
     */
    public void streamChat(BaseChatCompletion chat, EventSourceListener eventSourceListener, String domain, String url) {
        if (Objects.isNull(eventSourceListener)) {
            throw new LLMException("参数异常：EventSourceListener不能为空");
        }
        chat.setStream(true);
        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            Request request = new Request.Builder().url(domain + url)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            new ObjectMapper().writeValueAsString(chat))).build();
            factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常：", e);
            e.printStackTrace();
        }
    }

    /**
     * Gitee模式方舟 使用
     * 流式响应
     */
    public Response streamChat(BaseChatCompletion chat, String url) {
        chat.setStream(true);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            /**
             * {"top_p":0.7,"frequency_penalty":0,"stream":true,"max_tokens":10240,"temperature":0.6,"messages":[{"role":"user","content":"你是谁？"}],"model":"DeepSeek-R1-Distill-Qwen-32B","extra_body":{"top_k":50}}
             */
            String jsonBody = objectMapper.writeValueAsString(chat);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);

            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("X-Failover-Enabled", "true")
                    .addHeader("Accept", "text/event-stream")
                    .build();

            // 执行请求
            return okHttpClient.newCall(request).execute();
        } catch (Exception e) {
            log.error("Stream chat request failed: ", e);
            return null;
        }
    }


    /**
     * ollama/coze 使用
     * 流式响应
     *
     * @param
     * @param
     * @param
     */
    public Response streamChat(BaseChatCompletion chat, String domain, String url) {
        chat.setStream(true);
        try {
            Request request = new Request.Builder().url(domain + url)
                    .addHeader(AuthConstant.JWT_TOKEN_HEADER, AuthConstant.JWT_TOKEN_PREFIX + this.apiKey)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            new ObjectMapper().writeValueAsString(chat))).build();
//            if (ObjectUtil.isNotNull(this.apiKey)) {
//                request.newBuilder().addHeader(AuthConstant.JWT_TOKEN_HEADER, AuthConstant.JWT_TOKEN_PREFIX + this.apiKey);
//            }
            return okHttpClient.newCall(request).execute();
        } catch (Exception e) {
            log.error("请求参数解析异常：", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建默认的OkHttpClient
     */
    private OkHttpClient okHttpClient(HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new LocalLMLogger());
        httpLoggingInterceptor.setLevel(level);
        return new OkHttpClient.Builder()
                .addInterceptor(new LocalLMInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    public static final class Builder {
        private String accessToken;

        private String apiKey;

        private OkHttpClient okHttpClient;
        private HttpLoggingInterceptor.Level logLevel;

        public Builder() {
        }

        public Builder accessToken(String val) {
            accessToken = val;
            return this;
        }

        public Builder apiKey(String val) {
            apiKey = val;
            return this;
        }

        public Builder logLevel(HttpLoggingInterceptor.Level val) {
            logLevel = val;
            return this;
        }

        public Builder okHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public LocalLMClient build() {
            return new LocalLMClient(this);
        }
    }

}

