package com.hula.ai.llm.wenxin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.wenxin.entity.request.ChatCompletion;
import com.hula.ai.llm.wenxin.entity.request.ChatCompletionMessage;
import com.hula.ai.llm.wenxin.entity.request.ImagesBody;
import com.hula.ai.llm.wenxin.entity.response.AuthResponse;
import com.hula.ai.llm.wenxin.entity.response.ChatResponse;
import com.hula.ai.llm.wenxin.entity.response.ImageResponse;
import com.hula.ai.llm.wenxin.enums.ModelEnum;
import com.hula.ai.llm.wenxin.interceptor.WenXinInterceptor;
import com.hula.ai.llm.wenxin.interceptor.WenXinLogger;
import com.hula.domain.vo.res.ApiResult;
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
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 文心一言
 * 文档地址：https://cloud.baidu.com/doc/WENXINWORKSHOP/s/flfmc9do2
 *
 * @author Yang
 * @version 1.0.0
 * @date 2023/8/4 13:03
 * 得其道
 * 乾乾
 */
@NoArgsConstructor(force = true)
@Slf4j
public class WenXinClient {

    @NotNull
    @Getter
    @Setter
    private String accessToken;

    @NotNull
    @Getter
    @Setter
    private String apiKey;
    @NotNull
    @Getter
    @Setter
    private String secretKey;
    @Getter
    @Setter
    private LocalDate accessTokenReceiveDate;
    @Getter
    private OkHttpClient okHttpClient;

    private WenXinClient(Builder builder) {
        apiKey = builder.apiKey;
        secretKey = builder.secretKey;
        if (Objects.isNull(builder.okHttpClient)) {
            log.info("提示：禁止在生产环境使用BODY级别日志，可以用：NONE,BASIC,HEADERS");
            if (Objects.isNull(builder.logLevel)) {
                builder.logLevel = HttpLoggingInterceptor.Level.HEADERS;
            }
            builder.okHttpClient = this.okHttpClient(builder.logLevel);
        }
        okHttpClient = builder.okHttpClient;
        // 获取token
        accessToken = updateAccessTokenFromBaidu();
        if (StrUtil.isBlank(accessToken)) {
            throw new LLMException("构造错误: accessToken不能为空");
        }
    }

    /**
     * 同步响应
     *
     * @param messages
     * @param model
     * @return
     */
    public ApiResult<ChatResponse> chat(List<ChatCompletionMessage> messages, ModelEnum model) {
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();
        try {
            Request request = new Request.Builder().url(assembleUrl(model))
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            new ObjectMapper().writeValueAsString(chatCompletion))).build();
            Response response;
            response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if (ObjectUtil.isNotNull(jsonObject.get("error_msg"))) {
                log.error("文心一言大模型接口请求异常，异常原因：{}", jsonObject.getStr("error_msg"));
                return ApiResult.fail(-1, jsonObject.getStr("error_msg"));
            }
            return ApiResult.success(jsonObject.toBean(ChatResponse.class));

        } catch (Exception e) {
            log.error("请求参数解析异常：", e);
            e.printStackTrace();
            return ApiResult.fail(-1, "文心一言大模型接口请求异常");
        }
    }

    /**
     * 流式响应
     *
     * @param messages
     * @param eventSourceListener
     * @param model
     */
    public void streamChat(List<ChatCompletionMessage> messages, EventSourceListener eventSourceListener, ModelEnum model) {
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).stream(true).build();
        if (Objects.isNull(eventSourceListener)) {
            throw new LLMException("参数异常：EventSourceListener不能为空");
        }
        chatCompletion.setStream(true);
        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            Request request = new Request.Builder().url(assembleUrl(model))
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            new ObjectMapper().writeValueAsString(chatCompletion))).build();
            factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常：", e);
            e.printStackTrace();
        }
    }

    /**
     * 文本生成图片
     *
     * @param body 文本生成图片
     * @return
     */
    public ApiResult<ImageResponse> image(ImagesBody body) {
        try {
            Request request = new Request.Builder().url(assembleUrl(ModelEnum.STABLE_DIFFUSION_XL))
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            new ObjectMapper().writeValueAsString(body))).build();
            Response response;
            response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if (ObjectUtil.isNotNull(jsonObject.get("error_msg"))) {
                log.error("文心一言文生图大模型接口请求异常，异常原因：{}", jsonObject.getStr("error_msg"));
                return ApiResult.fail(-1, jsonObject.getStr("error_msg"));
            }
            return ApiResult.success(jsonObject.toBean(ImageResponse.class));

        } catch (Exception e) {
            log.error("请求参数解析异常：", e);
            e.printStackTrace();
            return ApiResult.fail(-1,"文心一言文生图大模型接口请求异常");
        }
    }

    /**
     * 验证token及获取模型调用地址
     *
     * @param model 模型信息
     * @return
     */
    private String assembleUrl(ModelEnum model) {
        accessToken = refreshAccessToken();
        return model.getApiHost() + "?access_token=" + accessToken;
    }

    /**
     * 获取token
     *
     * @return
     */
    public String updateAccessTokenFromBaidu() {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, StringPoolConstant.EMPTY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=" + apiKey
                        + "&client_secret=" + secretKey + "&grant_type=client_credentials")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        AuthResponse vo = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.body() != null) {
                vo = JSONUtil.parseObj(response.body().string()).toBean(AuthResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new LLMException("获取文心一言token失败");
        }
        if (vo != null) {
            accessToken = vo.getAccess_token();
            accessTokenReceiveDate = LocalDate.now();
            return accessToken;
        } else {
            throw new LLMException("获取文心一言token失败");
        }
    }

    /**
     * 刷新token
     *
     * @return
     */
    public String refreshAccessToken() {
        if (accessTokenReceiveDate.plusDays(25).isBefore(LocalDate.now())) {
            updateAccessTokenFromBaidu();
        }
        return accessToken;
    }

    /**
     * 创建默认的OkHttpClient
     */
    private OkHttpClient okHttpClient(HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new WenXinLogger());
        httpLoggingInterceptor.setLevel(level);
        return new OkHttpClient.Builder()
                .addInterceptor(new WenXinInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 构造
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @NotNull String accessToken;

        private @NotNull String apiKey;
        private @NotNull String secretKey;

        private OkHttpClient okHttpClient;
        private HttpLoggingInterceptor.Level logLevel;

        public Builder() {
        }

        public Builder accessToken(@NotNull String val) {
            accessToken = val;
            return this;
        }

        public Builder apiKey(@NotNull String val) {
            apiKey = val;
            return this;
        }

        public Builder secretKey(@NotNull String val) {
            secretKey = val;
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

        public WenXinClient build() {
            return new WenXinClient(this);
        }
    }

}
