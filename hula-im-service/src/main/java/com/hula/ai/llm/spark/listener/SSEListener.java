package com.hula.ai.llm.spark.listener;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hula.ai.client.enums.ChatContentEnum;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.service.GptService;
import com.hula.ai.framework.util.ApplicationContextUtil;
import com.hula.ai.llm.base.entity.ChatData;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.base.websocket.WebsocketServer;
import com.hula.ai.llm.base.websocket.constant.FunctionCodeConstant;
import com.hula.ai.llm.base.websocket.entity.WebSocketData;
import com.hula.ai.llm.spark.constant.SparkErrorCode;
import com.hula.ai.llm.spark.constant.StatusConstant;
import com.hula.ai.llm.spark.entity.request.ChatCompletionMessage;
import com.hula.ai.llm.spark.entity.request.ChatRequest;
import com.hula.ai.llm.spark.entity.response.ChatResponse;
import com.hula.ai.llm.spark.entity.response.ChatResponseChoices;
import com.hula.ai.llm.spark.entity.response.ChatResponseHeader;
import com.hula.ai.llm.spark.entity.response.ChatUsage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 讯飞星火回答监听 流式输出
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Slf4j
public class SSEListener extends WebSocketListener {
    private static final String FINISH = "[finish]";
    private final StringBuilder output = new StringBuilder();
    public ObjectMapper objectMapper;
    private ChatRequest request;
    private HttpServletResponse response;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private Long chatId;
    private String parentMessageId;
    private String conversationId;
    private String finishReason;
    private String version;
    private Boolean error;
    private String errTxt;
    private String uid;
    private Boolean isWs;

    public SSEListener(ChatRequest request, HttpServletResponse response, Long chatId, String parentMessageId, String uid, String version, Boolean isWs) {
        objectMapper = new ObjectMapper();
        // 排除值为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.request = request;
        this.response = response;
        this.chatId = chatId;
        this.parentMessageId = parentMessageId;
        this.uid = uid;
        this.version = version;
        this.isWs = isWs;
        this.error = false;
    }

    @Override
    public final void onOpen(@NotNull WebSocket webSocket, @NotNull Response rs) {
        log.info("讯飞星火建立连接成功");
        // 发送消息
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new LLMException("请求数据 SparkRequest 序列化失败");
        }
        webSocket.send(requestJson);
        if (!isWs) {
            response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    public final void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        ChatResponse chatResponse;
        // 解析响应
        try {
            chatResponse = objectMapper.readValue(text, ChatResponse.class);
        } catch (JsonProcessingException e) {
            webSocket.close(1000, "");
            throw new LLMException("响应数据 SparkResponse 解析失败：" + text);
        }
        ChatResponseHeader header = chatResponse.getHeader();
        if (null == header) {
            webSocket.close(1000, "");
            throw new LLMException("响应数据不完整 SparkResponse.header为null，完整响应：" + text);
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

        this.onMessage(content, usage, status, request, chatResponse, webSocket);

        // 最后一条结果，关闭连接
        if (StatusConstant.FINISH.equals(status)) {
            webSocket.close(1000, "");
            countDownLatch.countDown();
        }
    }

    private void onMessage(String content, ChatUsage usage, Integer status, ChatRequest chatRequest, ChatResponse chatResponse, WebSocket webSocket) {
        log.info("讯飞星火返回：{}", JSON.toJSONString(chatResponse));
        output.append(content);
        try {
            if (ObjectUtil.isNull(conversationId) && ObjectUtil.isNotNull(chatResponse.getHeader().getSid())) {
                conversationId = chatResponse.getHeader().getSid();
            }
            String text = output.toString();
            ChatData chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
                    .parentMessageId(parentMessageId)
                    .role(ChatRoleEnum.ASSISTANT.getValue()).content(text).build();
            if (isWs) {
                WebSocketData wsData = WebSocketData.builder().functionCode(FunctionCodeConstant.MESSAGE).message(chatData).build();
                WebsocketServer.sendMessageByUserId(uid, JSON.toJSONString(wsData));
            } else {
                response.getWriter().write(ObjectUtil.isNull(text) ? JSON.toJSONString(chatData) : "\n" + JSON.toJSONString(chatData));
                response.getWriter().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("消息错误", e);
            countDownLatch.countDown();
        } finally {
            if (StatusConstant.FINISH.equals(status)) {
                log.info("讯飞星火返回数据结束了");
                ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(conversationId).parentMessageId(parentMessageId)
                        .model(ChatModelEnum.SPARK.getValue()).modelVersion(version)
                        .content(output.toString()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.ASSISTANT.getValue()).finishReason(FINISH)
                        .status(ChatStatusEnum.SUCCESS.getValue()).appKey("").usedTokens(usage.getText().getTotalTokens().longValue())
                        .build();
                ApplicationContextUtil.getBean(GptService.class).saveChatMessage(chatMessage);
            }
        }
    }

    @SneakyThrows
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        log.error("讯飞星火连接异常，异常：{}", t.getMessage());
        ChatData chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
                .parentMessageId(parentMessageId)
                .role(ChatRoleEnum.ASSISTANT.getValue()).content("讯飞星火接口请求失败，无法响应！").contentType(ChatContentEnum.TEXT.getValue()).build();
        this.error = true;
        this.errTxt = "讯飞星火接口连接异常";
        this.response.getWriter().write(JSON.toJSONString(chatData));
        this.response.getWriter().flush();
        super.onFailure(webSocket, t, response);
        countDownLatch.countDown();
    }

    public CountDownLatch getCountDownLatch() {
        return this.countDownLatch;
    }


    public Boolean getError() {
        return error;
    }

    public String getErrTxt() {
        return errTxt;
    }

}
