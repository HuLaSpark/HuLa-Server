package com.hula.ai.llm.locallm.coze.listener;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hula.ai.client.enums.ChatContentEnum;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.service.GptService;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.framework.util.ApplicationContextUtil;
import com.hula.ai.llm.base.entity.ChatData;
import com.hula.ai.llm.base.websocket.WebsocketServer;
import com.hula.ai.llm.base.websocket.constant.FunctionCodeConstant;
import com.hula.ai.llm.base.websocket.entity.WebSocketData;
import com.hula.ai.llm.locallm.coze.entity.ChatResponse;
import com.hula.ai.llm.locallm.coze.enums.MessageTypeEnum;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * coze 流式监听处理
 *
 * @author: 云裂痕
 * @date: 2025/03/08
 * @version: 1.2.8
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
    private Boolean isCompleted = false;
    private List<String> docs;
    private List<String> followUps;

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
        this.docs = new ArrayList<>();
        this.followUps = new ArrayList<>();
        if (response == null) {
            log.error("客户端非sse推送");
            return;
        }
        if (!isWs) {
            response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        log.info("coze建立sse连接...");
    }

    /**
     * 流失回答
     */
    public Boolean streamChat(Response response) {
        ChatResponse chatMessageAccumulator = mapStreamToAccumulator(response)
                .doOnNext(accumulator -> {
                    if (ObjectUtil.isNull(accumulator.getContent())) {
                        return;
                    }
                    if (!isCompleted) {
                        output.append(accumulator.getContent()).toString();
                        // 向客户端发送信息
                        output();
                    }
                    if (accumulator.getType().equals(MessageTypeEnum.FOLLOW_UP.getValue())) {
                        followUps.add(accumulator.getContent());
                    }
                }).doOnComplete(System.out::println).lastElement().blockingGet();
        this.conversationId = chatMessageAccumulator.getConversationId();
        log.info("coze返回数据结束了:{}", JSON.toJSONString(chatMessageAccumulator));
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(conversationId).parentMessageId(parentMessageId)
                .model(ChatModelEnum.LOCALLM.getValue()).modelVersion(version)
                .content(output.toString()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.ASSISTANT.getValue()).finishReason(finishReason)
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey("").usedTokens(Long.valueOf(0L))
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

    public Flowable<ChatResponse> mapStreamToAccumulator(Response response) {
        return Flowable.create(emitter -> {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                emitter.onError(new RuntimeException("Response body is null"));
                return;
            }
            String line;
            while ((line = responseBody.source().readUtf8Line()) != null) {
                if (line.equals(StringPoolConstant.EMPTY) || line.equals(StringPoolConstant.NEWLINE)) {
                    continue;
                }
                if (Objects.equals(line, "[DONE]")) {
                    emitter.onComplete();
                    return;
                }
                if (line.equals("event:done")) {
                    emitter.onComplete();
                    return;
                }
                String completed = "event:conversation.message.completed";
                if (line.equals(completed)) {
                    isCompleted = true;
                }
                log.info("coze返回数据：" + line);
                if (!line.startsWith("data:")) {
                    continue;
                }
                line = line.substring(5);
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                Gson gson = new Gson();
                ChatResponse streamResponse = gson.fromJson(line, ChatResponse.class);
                if (streamResponse.getCode() != 0) {
                    emitter.onError(new BizException("Error: " + streamResponse.getMsg()));
                    return;
                }
                emitter.onNext(streamResponse);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }


}
