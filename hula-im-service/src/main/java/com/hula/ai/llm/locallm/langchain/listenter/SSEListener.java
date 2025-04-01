package com.hula.ai.llm.locallm.langchain.listenter;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
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
import com.hula.ai.llm.locallm.langchain.entity.ChatResponse;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * langchain 流式监听处理
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Slf4j
@NoArgsConstructor(force = true)
public class SSEListener extends EventSourceListener {
    private static final String FINISH = "[finish]";
    private long tokens;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private HttpServletResponse response;
    private SseEmitter sseEmitter;
    private StringBuffer output = new StringBuffer();
    private Long chatId;
    private String parentMessageId;
    private String conversationId;
    private String finishReason;
    private String version;
    private String knowledge;
    private Boolean error;
    private String errTxt;
    private String uid;
    private Boolean isWs = false;
    private String prompt;
    private List<String> docs;

    public SSEListener(HttpServletResponse response, SseEmitter sseEmitter, Long chatId, String parentMessageId, String version, String knowledge, String prompt, String uid, Boolean isWs) {
        this.response = response;
        this.sseEmitter = sseEmitter;
        this.chatId = chatId;
        this.parentMessageId = parentMessageId;
        if (!isWs) {
            this.response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        this.version = version;
        this.knowledge = knowledge;
        this.isWs = isWs;
        this.uid = uid;
        this.error = false;
        this.prompt = prompt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response rp) {
        if (response == null) {
            log.error("客户端非sse推送");
            return;
        }
        if (!isWs) {
            response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        log.info("{}建立sse连接...", "langchain");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("SSE返回，模型：{}，ID：{}，TYPE：{}，数据：{}", "langchain", id, type, data);
        ChatData chatData;
        String text;
        try {
            text = ObjectUtil.isNull(knowledge) ? handleLangchain(type, data) : handleLangchainKnowledge(type, data);
            if (text.equals(FINISH)) {
                log.info("{}返回数据结束了", "langchain");
                sseEmitter.complete();
                ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(conversationId).parentMessageId(parentMessageId)
                        .model(ChatModelEnum.LOCALLM.getValue()).modelVersion(version)
                        .content(output.toString()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.ASSISTANT.getValue()).finishReason(finishReason)
                        .status(ChatStatusEnum.SUCCESS.getValue()).usedTokens(tokens)
                        .build();
                ApplicationContextUtil.getBean(GptService.class).saveChatMessage(chatMessage);
                return;
            }
            chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
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
            log.error("消息错误", e);
            eventSource.cancel();
            countDownLatch.countDown();
            throw new BizException();
        }
    }


    /**
     * 处理Langchain流式返回
     *
     * @return
     */
    private String handleLangchain(String type, String data) {
        ChatResponse chatResponse = JSON.parseObject(data, ChatResponse.class);;
        conversationId = chatResponse.getMessageId();
        output.append(chatResponse.getText()).toString();
        return output.toString();
    }

    /**
     * 处理Langchain知识库流式返回
     *
     * @return
     */
    private String handleLangchainKnowledge(String type, String data) {
        ChatResponse chatResponse = JSON.parseObject(data, ChatResponse.class);
        if (ObjectUtil.isNotNull(chatResponse.getDocs())) {
            tokens = output.toString().length() + prompt.length();
            finishReason = FINISH;
            conversationId = UUID.fastUUID().toString();
            docs = chatResponse.getDocs();
            return FINISH;
        }
        String content = chatResponse.getAnswer();
        output.append(content).toString();
        return output.toString();
    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("{}关闭sse连接，流式输出返回值总共{}tokens", "langchain", tokens());
        tokens = output.toString().length() + prompt.length();
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(conversationId).parentMessageId(parentMessageId)
                .model(ChatModelEnum.LOCALLM.getValue()).modelVersion(version)
                .content(output.toString()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.ASSISTANT.getValue()).finishReason(finishReason)
                .status(ChatStatusEnum.SUCCESS.getValue()).usedTokens(tokens)
                .build();
        ApplicationContextUtil.getBean(GptService.class).saveChatMessage(chatMessage);
        eventSource.cancel();
        countDownLatch.countDown();
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (ObjectUtil.isNotNull(response) && Objects.nonNull(response.body())) {
            log.error("sse连接异常data.body:{}，异常:{}", response.body().string(), t);
        } else {
            log.error("sse连接异常data:{}，异常:{}", response, t);
        }
        ChatData chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
                .parentMessageId(parentMessageId)
                .role(ChatRoleEnum.ASSISTANT.getValue()).content("AI大模型接口请求失败，无法响应！").contentType(ChatContentEnum.TEXT.getValue()).build();
        this.error = true;
        this.errTxt = "大模型接口连接异常";
        this.response.getWriter().write(JSON.toJSONString(chatData));
        this.response.getWriter().flush();
        eventSource.cancel();
        countDownLatch.countDown();
    }

    public CountDownLatch getCountDownLatch() {
        return this.countDownLatch;
    }

    /**
     * tokens
     *
     * @return
     */
    public long tokens() {
        return tokens;
    }

    public Boolean getError() {
        return error;
    }

    public String getErrTxt() {
        return errTxt;
    }

}
