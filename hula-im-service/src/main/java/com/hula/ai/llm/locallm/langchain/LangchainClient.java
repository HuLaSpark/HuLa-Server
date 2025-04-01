package com.hula.ai.llm.locallm.langchain;

import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.model.dto.ModelDTO;
import com.hula.ai.llm.locallm.LocalLMClient;
import com.hula.ai.llm.locallm.langchain.constant.ApiConstant;
import com.hula.ai.llm.locallm.langchain.entity.ChatCompletion;
import com.hula.ai.llm.locallm.langchain.entity.ChatCompletionMessage;
import com.hula.ai.llm.locallm.langchain.enums.ModelEnum;
import com.hula.ai.llm.locallm.langchain.listenter.SSEListener;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * LangChain-Chatchat client
 *
 * @author: 云裂痕
 * @date: 2023/12/4
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Slf4j
@Service
public class LangchainClient {
    private static LocalLMClient localLMClient;

    @Autowired
    public LangchainClient(LocalLMClient localLMClient) {
        LangchainClient.localLMClient = localLMClient;
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
        SSEListener sseListener = new SSEListener(response, sseEmitter, chatId, conversationId, version, modelDTO.getKnowledge(), prompt, uid, isWs);
        List<ChatCompletionMessage> history = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            ChatCompletionMessage message = ChatCompletionMessage.builder().content(v.getContent()).role(v.getRole()).build();
            history.add(message);
        });
        String model = ObjectUtil.isNotNull(version) ? version : ApiConstant.DEFAULT_MODEL;
        ChatCompletion chat = ChatCompletion.builder()
                .query(prompt)
                .knowledgeBaseName(ApiConstant.DEFAULT_KNOWLEDGE)
                .history(history)
                .modelName(model)
                .build();
        String domain = ObjectUtil.isNotNull(modelDTO.getModelUrl()) ? modelDTO.getModelUrl() : ApiConstant.BASE_DOMAIN;
        ModelEnum modelUrl = ObjectUtil.isNotNull(modelDTO.getKnowledge()) ? ModelEnum.KNOWLEDGE : ModelEnum.LLM;
        localLMClient.streamChat(chat, sseListener, domain, modelUrl.getUrl());
        sseListener.getCountDownLatch().await();
        return sseListener.getError();
    }

}

