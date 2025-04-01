package com.hula.ai.llm.chatglm;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.hula.ai.llm.chatglm.listener.SSEListener;
import com.hula.exception.BizException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.image.CreateImageRequest;
import com.zhipu.oapi.service.v4.image.ImageApiResponse;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatFunction;
import com.zhipu.oapi.service.v4.model.ChatFunctionParameters;
import com.zhipu.oapi.service.v4.model.ChatTool;
import com.zhipu.oapi.service.v4.model.ChatToolType;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智谱清言client
 * 文档地址：https://open.bigmodel.cn/dev/api
 *
 * @author: 云裂痕
 * @date: 2023/12/4
 * 得其道
 * 乾乾
 */
@Slf4j
@NoArgsConstructor(force = true)
public class ChatGLMClient {
    private static final String requestIdTemplate = "master-%d";
    @Getter
    @Setter
    private String appKey;
    @Getter
    @Setter
    private String appSecret;
    @Getter
    @Setter
    private String apiSecretKey;
    @Getter
    private ClientV4 clientV4;

    private ChatGLMClient(Builder builder) {
        if (StrUtil.isBlank(builder.appKey)) {
            throw new BizException("构造错误: accessToken不能为空");
        }
        appKey = builder.appKey;
        appSecret = builder.appSecret;
        apiSecretKey = builder.apiSecretKey;
        if (StrUtil.isNotBlank(builder.apiSecretKey) && StrUtil.isBlank(builder.appSecret)) {
            this.clientV4 = new ClientV4.Builder(apiSecretKey).build();
        } else {
            this.clientV4 = new ClientV4.Builder(appKey, appSecret).build();
        }
    }

    /**
     * 同步响应
     *
     * @param request
     */
    public ModelApiResponse chat(ChatCompletionRequest request) {
        // 插件调用
//        request = functionCall(request);
        request.setStream(Boolean.FALSE);
        request.setInvokeMethod(Constants.invokeMethod);
        request.setRequestId(String.format(requestIdTemplate, System.currentTimeMillis()));
        ModelApiResponse invokeModelApiResp = this.clientV4.invokeModelApi(request);
        try {
            return invokeModelApiResp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 流式响应
     *
     * @param request
     */
    public Boolean streamChat(HttpServletResponse response, ChatCompletionRequest request, Long chatId, String parentMessageId, String version, String uid, Boolean isWs) {
        request.setStream(Boolean.TRUE);
        request.setRequestId(String.format(requestIdTemplate, System.currentTimeMillis()));
        ModelApiResponse sseModelApiResp = this.clientV4.invokeModelApi(request);
        SSEListener sseListener = new SSEListener(response, chatId, parentMessageId, version, uid, isWs);
        return sseListener.streamChat(sseModelApiResp);
    }

    /**
     * 生成图片
     */
    private void image() {
        CreateImageRequest createImageRequest = new CreateImageRequest();
        createImageRequest.setModel(Constants.ModelCogView);
//        createImageRequest.setPrompt("画一个温顺可爱的小狗");
        ImageApiResponse imageApiResponse = this.clientV4.createImage(createImageRequest);
        System.out.println("imageApiResponse:" + JSON.toJSONString(imageApiResponse));
    }


    // 插件调用
    private ChatCompletionRequest functionCall(ChatCompletionRequest request) {
        // 函数调用参数构建部分
        List<ChatTool> chatToolList = new ArrayList<>();
        ChatTool chatTool = new ChatTool();
        chatTool.setType(ChatToolType.FUNCTION.value());
        ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
        chatFunctionParameters.setType("object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("location", new HashMap<String, Object>() {{
            put("type", "string");
            put("description", "城市，如：北京");
        }});
        properties.put("unit", new HashMap<String, Object>() {{
            put("type", "string");
            put("enum", new ArrayList<String>() {{
                add("celsius");
                add("fahrenheit");
            }});
        }});
        chatFunctionParameters.setProperties(properties);
        ChatFunction chatFunction = ChatFunction.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .parameters(chatFunctionParameters)
                .build();
        chatTool.setFunction(chatFunction);
        chatToolList.add(chatTool);
        request.setTools(chatToolList);
        request.setToolChoice("auto");
        return request;
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
        private String appKey;
        private String appSecret;
        private String apiSecretKey;

        public Builder() {
        }

        public Builder appKey(String val) {
            appKey = val;
            return this;
        }

        public Builder appSecret(String val) {
            appSecret = val;
            return this;
        }

        public Builder apiSecretKey(String val) {
            apiSecretKey = val;
            return this;
        }

        public ChatGLMClient build() {
            return new ChatGLMClient(this);
        }
    }

}
