package com.hula.ai.llm.spark.listener;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.spark.constant.SparkErrorCode;
import com.hula.ai.llm.spark.entity.request.ChatCompletionMessage;
import com.hula.ai.llm.spark.entity.request.ChatRequest;
import com.hula.ai.llm.spark.entity.response.ChatResponse;
import com.hula.ai.llm.spark.entity.response.ChatResponseChoices;
import com.hula.ai.llm.spark.entity.response.ChatResponseHeader;
import com.hula.ai.llm.spark.entity.response.ChatUsage;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 讯飞星火 监听
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class BaseListener extends WebSocketListener {

    private ChatRequest chatRequest;

    public ObjectMapper objectMapper;

    public BaseListener() {
        objectMapper = new ObjectMapper();
        // 排除值为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public final void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        // 发送消息
        String requestJson = null;
        try {
            requestJson = objectMapper.writeValueAsString(chatRequest);
        } catch (JsonProcessingException e) {
            throw new LLMException("请求数据 SparkRequest 序列化失败");
        }
        webSocket.send(requestJson);
    }

    @Override
    public final void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        ChatResponse chatResponse;
        // 解析响应
        try {
            chatResponse = objectMapper.readValue(text, ChatResponse.class);
        } catch (JsonProcessingException e) {
            webSocket.close(1000, "");
            throw new LLMException(500, "响应数据 SparkResponse 解析失败：" + text, e);
        }
        ChatResponseHeader header = chatResponse.getHeader();
        if (null == header) {
            webSocket.close(1000, "");
            throw new LLMException(500, "响应数据不完整 SparkResponse.header为null，完整响应：" + text);
        }

        // 业务状态判断
        Integer code = header.getCode();
        if (0 != code) {
            webSocket.close(1000, "");
            throw new LLMException("websocket 连接失败，错误原因: " + SparkErrorCode.RESPONSE_ERROR_MAP.get(code));
        }

        // 回答文本
        ChatResponseChoices choices = chatResponse.getPayload().getChoices();
        List<ChatCompletionMessage> messages = choices.getText();
        StringBuilder stringBuilder = new StringBuilder();
        for (ChatCompletionMessage message : messages) {
            stringBuilder.append(message.getContent());
        }
        String content = stringBuilder.toString();

        ChatUsage usage = chatResponse.getPayload().getUsage();
        Integer status = header.getStatus();

        this.onMessage(content, usage, status, chatRequest, chatResponse, webSocket);

        // 最后一条结果，关闭连接
        if (2 == status) {
            webSocket.close(1000, "");
        }
    }

    /**
     * 收到回答时会调用此方法
     *
     * @param content       回答内容
     * @param usage         tokens消耗统计
     * @param status        会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果，当status为2时，webSocket连接会自动关闭
     * @param chatRequest  本次会话的请求参数
     * @param chatResponse 本次回调的响应数据
     * @param webSocket     本次会话的webSocket连接
     */
    public void onMessage(String content, ChatUsage usage, Integer status, ChatRequest chatRequest, ChatResponse chatResponse, WebSocket webSocket) {
        // 重写此方法，实现业务逻辑
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        this.onError(webSocket, t, response);
        throw new LLMException("讯飞星火api连接异常：" + t.getMessage());
    }

    public void onError(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
    }


    public ChatRequest getSparkRequest() {
        return chatRequest;
    }

    public void setSparkRequest(ChatRequest chatRequest) {
        this.chatRequest = chatRequest;
    }
}
