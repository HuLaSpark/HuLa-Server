package com.hula.ai.llm.locallm.gitee.listener;

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
import com.hula.ai.llm.locallm.gitee.resp.ChatCompletionResponse;
import com.hula.ai.llm.locallm.gitee.resp.Choice;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor(force = true)
public class SSEListener extends EventSourceListener {
    private HttpServletResponse response;
    private Long chatId;
    // 父消息ID与对话ID（此处为了兼容原逻辑，将两者置为同一值）
    private String parentMessageId;
    private String conversationId;
    private String finishReason = "stop";
    private String version;
    private String uid;
    private Boolean isWs = false;
    private Boolean isCompleted = false;
    private final StringBuilder fullResponse = new StringBuilder();
    public CountDownLatch latch = new CountDownLatch(1);

    // 滑动超时相关字段
    private volatile long lastReceivedTime = System.currentTimeMillis();
    private final long idleTimeoutMs;
    private ThreadPoolTaskExecutor scheduler;
    private EventSource eventSource; // 保存 eventSource 对象

    /**
     * 构造函数中传入 idleTimeoutMs（毫秒），用以实现滑动超时
     */
    public SSEListener(HttpServletResponse response, Long chatId, String parentMessageId, String version, String uid, Boolean isWs, long idleTimeoutMs) {
        this.response = response;
        this.chatId = chatId;
        this.parentMessageId = parentMessageId;
        this.version = version;
        this.uid = uid;
        this.isWs = isWs;
        this.idleTimeoutMs = idleTimeoutMs;
        this.scheduler = ApplicationContextUtil.getBean("timeThreadPool", ThreadPoolTaskExecutor.class);

        try {
            if (response == null) {
                log.error("客户端非 SSE 推送");
                return;
            }
            if (!isWs) {
                response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Connection", "keep-alive");
                response.setHeader("X-Accel-Buffering", "no");
            }
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(HttpStatus.OK.value());

            log.info("SSE 连接已建立... chatId: {}, parentMessageId: {}", chatId, parentMessageId);
        } catch (Exception e) {
            log.error("SSE 连接初始化失败", e);
            handleError("SSE connection initialization failed");
        }

        // 使用 ThreadPoolTaskExecutor 定时检查超时
        scheduler.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    long timeElapsed = System.currentTimeMillis() - lastReceivedTime;

                    if (timeElapsed > idleTimeoutMs) {
                        log.warn("Idle timeout reached for chatId: {}", chatId);
                        handleError("Connection idle timeout");
                        if (eventSource != null) {
                            eventSource.cancel();
                        }
                        completeLatch();
                        break;
                    }
                    long sleepTime = Math.min(idleTimeoutMs - timeElapsed, idleTimeoutMs / 2);
                    if (sleepTime > 0) {
                        TimeUnit.MILLISECONDS.sleep(sleepTime);
                    }
                }
            } catch (InterruptedException e) {
                log.error("定时任务被中断", e);
            }
        });
    }

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        this.eventSource = eventSource;
        try {
            log.info("SSE 连接已打开，响应码：{}, chatId: {}",
                    response != null ? response.code() : "unknown", chatId);
        } catch (Exception e) {
            log.error("SSE onOpen 处理失败", e);
        }
    }

    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        // 每收到一次消息就更新最后接收时间，重置空闲计时
        lastReceivedTime = System.currentTimeMillis();
        try {
            log.debug("收到 SSE 事件 - chatId: {}, id: {}, type: {}, data: {}",
                    chatId, id, type, data);

            if (data == null || data.trim().isEmpty()) {
                log.debug("忽略空消息");
                return;
            }

            if ("[DONE]".equals(data)) {
                log.info("收到结束标志 [DONE]，chatId: {}", chatId);
                handleCompletion(eventSource);
                return;
            }

            // 解析并处理返回的数据
            ChatCompletionResponse chatResponse = JSON.parseObject(data, ChatCompletionResponse.class);
            this.conversationId = chatResponse.getId();
            if (chatResponse != null && ObjectUtil.isNotNull(chatResponse.getChoices())) {
                Choice choice = chatResponse.getChoices().get(0);
                String content = choice.getDelta().getContent();
                if (content != null) {
                    try {
                        fullResponse.append(content);
                        sendMessage(fullResponse.toString());
                    } catch (IOException e) {
                        log.error("消息写入失败，chatId: {}", chatId, e);
                        handleError("Failed to write message");
                    }
                }
            }
        } catch (Exception e) {
            log.error("SSE 事件处理失败，chatId: {}", chatId, e);
            handleError("Event processing failed");
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        try {
            log.info("SSE 连接已关闭，chatId: {}", chatId);
            completeLatch();
        } catch (Exception e) {
            log.error("SSE onClosed 处理失败", e);
        }
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (t != null && t.getMessage() != null && t.getMessage().contains("Socket closed")) {
            log.debug("SSE 连接已完成并关闭，chatId: {}", chatId);
        } else {
            log.error("SSE 连接失败，chatId: {}, error: {}", chatId, t != null ? t.getMessage() : "unknown", t);
            if (response != null) {
                log.error("响应状态：{}, chatId: {}", response.code(), chatId);
            }
            handleError("Connection failed");
        }
        if (eventSource != null) {
            eventSource.cancel();
        }
        completeLatch();
    }

    private void handleCompletion(EventSource eventSource) {
        try {
            log.info("SSE 流完成，chatId: {}, 响应长度: {}", chatId, fullResponse.length());
            // 保存消息记录
            saveChatMessage();
            eventSource.cancel();
            completeLatch();
            log.info("SSE 处理完成，chatId: {}", chatId);
        } catch (Exception e) {
            log.error("处理完成时发生错误，chatId: {}", chatId, e);
            handleError("Completion handling failed");
        }
    }

    private void handleError(String errorMessage) {
        try {
            if (!response.isCommitted()) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                sendMessage("error: " + errorMessage);
            }
        } catch (Exception e) {
            log.error("错误处理失败", e);
        }
    }

    private void sendMessage(String text) throws IOException {
        try {
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
            log.error("发送消息失败，chatId: {}", chatId, e);
            throw e;
        }
    }

    private void saveChatMessage() {
        try {
            ChatMessageCommand chatMessage = ChatMessageCommand.builder()
                    .chatId(chatId)
                    .messageId(conversationId)
                    .parentMessageId(parentMessageId)
                    .model(ChatModelEnum.LOCALLM.getValue())
                    .modelVersion(version)
                    .content(fullResponse.toString())
                    .contentType(ChatContentEnum.TEXT.getValue())
                    .role(ChatRoleEnum.ASSISTANT.getValue())
                    .finishReason(finishReason)
                    .status(ChatStatusEnum.SUCCESS.getValue())
                    .appKey("")
                    .usedTokens(Long.valueOf(0L))
                    .build();

            log.info("保存聊天消息，chatId: {}, messageId: {}", chatId, conversationId);
            ApplicationContextUtil.getBean(GptService.class).saveChatMessage(chatMessage);
        } catch (Exception e) {
            log.error("保存聊天消息失败，chatId: {}", chatId, e);
        }
    }

    private void completeLatch() {
        if (!isCompleted) {
            latch.countDown();
            isCompleted = true;
        }
    }
}
