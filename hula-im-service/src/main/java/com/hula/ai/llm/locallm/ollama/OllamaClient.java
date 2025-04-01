package com.hula.ai.llm.locallm.ollama;

import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.model.dto.ModelDTO;
import com.hula.ai.llm.locallm.LocalLMClient;
import com.hula.ai.llm.locallm.ollama.constant.ApiConstant;
import com.hula.ai.llm.locallm.ollama.entity.ChatCompletion;
import com.hula.ai.llm.locallm.ollama.entity.ChatCompletionMessage;
import com.hula.ai.llm.locallm.ollama.enums.ModelEnum;
import com.hula.ai.llm.locallm.ollama.listener.SSEListener;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Ollama client
 *
 * @author: 云裂痕
 * @date: 2023/12/4
 * @version: 1.0.0
 * https://github.com/ollama/ollama/blob/main/docs/api.md
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Slf4j
@Service
public class OllamaClient {
    private static LocalLMClient localLMClient;

    @Autowired
    public OllamaClient(LocalLMClient localLMClient) {
        OllamaClient.localLMClient = localLMClient;
    }

    /**
     * 构建请求
     *
     * @param chatMessages
     * @param prompt
     * @param version
     * @param modelDTO
     */
    @SneakyThrows
    public Boolean buildChatCompletion(HttpServletResponse response, SseEmitter sseEmitter, Long chatId, String conversationId, Boolean isWs, String uid,
									   List<ChatMessageDTO> chatMessages, String prompt, String version, ModelDTO modelDTO) {
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            ChatCompletionMessage message = ChatCompletionMessage.builder().content(v.getContent()).role(v.getRole()).build();
            messages.add(message);
        });
        String model = ObjectUtil.isNotNull(version) ? version : ApiConstant.DEFAULT_MODEL;
        ChatCompletion chat = ChatCompletion.builder()
                .model(model).messages(messages)
                .build();
        String domain = ObjectUtil.isNotNull(modelDTO.getModelUrl()) ? modelDTO.getModelUrl() : ApiConstant.BASE_DOMAIN;
        ModelEnum modelUrl = ObjectUtil.isNotNull(modelDTO.getKnowledge()) ? ModelEnum.LLM : ModelEnum.LLM;
        Response callResponse = localLMClient.streamChat(chat, domain, modelUrl.getUrl());
        if (callResponse == null || callResponse.code() != 200) {
            log.error("ollama流式响应失败: " + callResponse.body().string());
            return true;
        }
        SSEListener sseListener = new SSEListener(response, chatId, conversationId, version, uid, isWs);
        return sseListener.streamChat(callResponse);
    }

}

