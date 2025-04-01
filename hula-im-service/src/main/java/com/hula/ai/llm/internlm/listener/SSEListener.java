package com.hula.ai.llm.internlm.listener;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hula.ai.client.enums.ChatContentEnum;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.service.GptService;
import com.hula.ai.framework.util.ApplicationContextUtil;
import com.hula.ai.llm.base.entity.ChatData;
import com.hula.ai.llm.base.websocket.WebsocketServer;
import com.hula.ai.llm.base.websocket.constant.FunctionCodeConstant;
import com.hula.ai.llm.base.websocket.entity.WebSocketData;
import com.hula.ai.llm.internlm.entity.response.ChatStreamResponse;
import com.hula.exception.BizException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 书生浦语 流式监听处理
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道 乾乾
 */
@Slf4j
@NoArgsConstructor(force = true)
public class SSEListener {
    private HttpServletResponse response;
    private StringBuffer output = new StringBuffer();
    private Long chatId;
    private String parentMessageId;
    private String conversationId;
    private String finishReason = "stop";
    private String version;
    private Boolean error;
    private String errTxt;
    private String uid;
    private Boolean isWs = false;

    /**
     * 流式响应
     *
     */
    public SSEListener(HttpServletResponse response, Long chatId, String parentMessageId, String version, String uid, Boolean isWs) {
        this.response = response;
        this.chatId = chatId;
        this.parentMessageId = parentMessageId;
        this.version = version;
        this.uid = uid;
        this.isWs = isWs;
        if (response == null) {
            log.error("客户端非sse推送");
            return;
        }
        if (!isWs) {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        log.info("书生浦语建立sse连接...");
    }

    /**
     * 流失回答
     *
     */
    public Boolean streamChat(Response response) {
        ChatStreamResponse chatMessageAccumulator = mapStreamToAccumulator(response)
                .doOnNext(accumulator -> {
                    if (accumulator.getChoices() != null && accumulator.getChoices().get(0).getMessage().getContent() != null) {
                        log.info("书生浦语返回，数据：{}", accumulator.getChoices().get(0).getMessage().getContent());
                        output.append(accumulator.getChoices().get(0).getMessage().getContent()).toString();
                        // 向客户端发送信息
                        output();
                    }
                }).doOnComplete(System.out::println).lastElement().blockingGet();
        this.conversationId = chatMessageAccumulator.getId();
        log.info("书生浦语回数据结束了:{}", JSON.toJSONString(chatMessageAccumulator));
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(conversationId).parentMessageId(parentMessageId)
                .model(ChatModelEnum.INTERNLM.getValue()).modelVersion(version)
                .content(output.toString()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.ASSISTANT.getValue()).finishReason(finishReason)
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey("").usedTokens(0L)
                .build();
        ApplicationContextUtil.getBean(GptService.class).saveChatMessage(chatMessage);
        return false;

    }

    private void output() {
        try {
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
        } catch (IOException e) {
            log.error("消息错误", e);
            throw new BizException();
        }
    }

    public static Flowable<ChatStreamResponse> mapStreamToAccumulator(Response response) {
        return Flowable.create(emitter -> {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                emitter.onError(new RuntimeException("Response body is null"));
                return;
            }
            String line;
            while ((line = responseBody.source().readUtf8Line()) != null) {
                if (line.startsWith("data:")) {
                    line = line.substring(5);
                    line = line.trim();
                }
                if (Objects.equals(line, "[DONE]")) {
                    emitter.onComplete();
                    return;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                Gson gson = new Gson();
                ChatStreamResponse streamResponse = gson.fromJson(line, ChatStreamResponse.class);
                emitter.onNext(streamResponse);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

}
