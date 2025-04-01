package com.hula.ai.llm.locallm.gitee;

import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.model.dto.ModelDTO;
import com.hula.ai.llm.locallm.LocalLMClient;
import com.hula.ai.llm.locallm.gitee.constant.ApiConstant;
import com.hula.ai.llm.locallm.gitee.listener.SSEListener;
import com.hula.ai.llm.locallm.gitee.req.ChatCompletion;
import com.hula.ai.llm.locallm.gitee.req.ExtraBody;
import com.hula.ai.llm.locallm.gitee.req.Message;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiteeClient {
    // 这里只用于设置连接和写入的超时时间
    private static final int TIMEOUT_SECONDS = 60;
    private static final int MAX_TOKENS = 10240;
    private static final double TEMPERATURE = 0.6;
    private static final double TOP_P = 0.7;
    private static final int TOP_K = 50;
    private static final int FREQUENCY_PENALTY = 0;

    // idleTimeout 为滑动超时（单位：毫秒），只有在长时间没有收到数据时才断开连接
    private static final long IDLE_TIMEOUT_MS = 60 * 1000L;

    private final LocalLMClient localLMClient;
    private final OkHttpClient client = createHttpClient();

    private OkHttpClient createHttpClient() {
        // 对于流式长连接，readTimeout 设置为0（无限超时），其它连接/写入超时照常设置
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public Boolean buildChatCompletion(
            HttpServletResponse response,
            SseEmitter sseEmitter,
            Long chatId,
            String conversationId,
            Boolean isWs,
            String uid,
            List<ChatMessageDTO> chatMessages,
            String prompt,
            String version,
            ModelDTO modelDTO) {

        try {
            ChatCompletion chat = buildChatRequest(chatMessages, modelDTO.getVersion());
            String domain = ObjectUtil.isNotNull(modelDTO.getModelUrl()) ? modelDTO.getModelUrl() : ApiConstant.DEAFAULT_DOMAIN;
            return handleStreamResponse(
                    response,
                    chatId,
                    conversationId,
                    version,
                    uid,
                    isWs,
                    chat,
                    domain
            );
        } catch (Exception e) {
            log.error("Error in buildChatCompletion: ", e);
            return true;
        }
    }

    private ChatCompletion buildChatRequest(List<ChatMessageDTO> chatMessages, String model) {
        List<Message> messages = chatMessages.stream()
                .map(msg -> new Message(msg.getRole(), msg.getContent()))
                .collect(Collectors.toList());
        ExtraBody extraBody = new ExtraBody();
        extraBody.setTopK(TOP_K);

        return ChatCompletion.builder()
                .model(model)
                .maxTokens(MAX_TOKENS)
                .temperature(TEMPERATURE)
                .topP(TOP_P)
                .extraBody(extraBody)
                .frequencyPenalty(FREQUENCY_PENALTY)
                .messages(messages)
                .build();
    }

    private Boolean handleStreamResponse(
            HttpServletResponse response,
            Long chatId,
            String conversationId,
            String version,
            String uid,
            Boolean isWs,
            ChatCompletion chat,
            String domain) throws Exception {

        Response callResponse = localLMClient.streamChat(chat, domain + ApiConstant.CHAT);
        if (!isSuccessResponse(callResponse)) {
            return true; // 响应错误时返回true
        }

        // 构造 SSEListener 时传入 idleTimeout 参数（单位毫秒）
        SSEListener listener = new SSEListener(response, chatId, conversationId, version, uid, isWs, IDLE_TIMEOUT_MS);
        EventSource eventSource = EventSources.createFactory(client)
                .newEventSource(callResponse.request(), listener);

        // 阻塞等待直到 SSEListener 内部完成（成功或错误均会通过 countDown 释放锁）
        listener.latch.await();
        return false;
    }

    private boolean isSuccessResponse(Response response) {
        if (response == null || response.code() != 200) {
            log.error("Stream response failed: {}",
                    response != null ? getResponseBody(response) : "Empty response");
            return false;
        }
        return true;
    }

    private String getResponseBody(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            log.error("Error reading response body", e);
            return "Error reading response body";
        }
    }
}
