package com.hula.ai.llm.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.openai.OpenAiClient;
import com.hula.ai.llm.openai.OpenAiStreamClient;
import com.hula.ai.llm.openai.entity.chat.ChatChoice;
import com.hula.ai.llm.openai.entity.chat.ChatCompletion;
import com.hula.ai.llm.openai.entity.chat.ChatCompletionResponse;
import com.hula.ai.llm.openai.entity.chat.Message;
import com.hula.ai.llm.openai.listener.SSEListener;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * openai 接口实现
 *
 * @author: 云裂痕
 * @date: 2025/03/13
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Service
public class OpenAIServiceImpl implements ModelService {
    private static OpenAiClient openAiClient;
    private static OpenAiStreamClient openAiStreamClient;

    public OpenAIServiceImpl(OpenAiClient openAiClient, OpenAiStreamClient openAiStreamClient) {
        OpenAIServiceImpl.openAiClient = openAiClient;
        OpenAIServiceImpl.openAiStreamClient = openAiStreamClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ObjectUtil.isNull(openAiClient)) {
            throw new BizException("ChatGpt无有效token，请切换其他模型进行聊天");
        }
        List<Message> openAiMessages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            Message currentMessage = Message.builder().content(v.getContent()).role(v.getRole()).build();
            openAiMessages.add(currentMessage);
        });
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(openAiMessages)
                .model(ObjectUtil.isNotNull(version) ? version : ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        ChatCompletionResponse response;
        try {
            response = openAiClient.chatCompletion(chatCompletion);
        } catch (Exception e) {
            throw new LLMException("OpenAi接口请求异常，请稍后再试");
        }
        ChatChoice choice = response.getChoices().get(0);
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getId())
                .model(ChatModelEnum.OPENAI.getValue()).modelVersion(response.getModel())
                .content(choice.getMessage().getContent()).role(choice.getMessage().getRole()).finishReason(choice.getFinishReason())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(openAiClient.getApiKey().get(0)).usedTokens(response.getUsage().getTotalTokens())
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (CollUtil.isEmpty(openAiStreamClient.getApiKey())) {
            throw new BizException("未加载到密钥信息");
        }
        List<Message> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            Message currentMessage = Message.builder().content(v.getContent()).role(v.getRole()).build();
            messages.add(currentMessage);
        });
        SSEListener sseListener = new SSEListener(response, sseEmitter, chatId, conversationId, ChatModelEnum.OPENAI.getValue(), version, uid.toString(), isWs);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(ObjectUtil.isNotNull(version) ? version : ChatCompletion.Model.GPT_3_5_TURBO_0613.getName())
                .build();
        openAiStreamClient.streamChatCompletion(completion, sseListener);
        if (isWs) {
            return false;
        }
        sseListener.getCountDownLatch().await();
        return sseListener.getError();
    }

	@Override
	public FileUploadResponse uploadFile(MultipartFile file) {
		return new FileUploadResponse();
	}

	@Override
	public JSONArray fileList() {
		return null;
	}

	@Override
	public Boolean deleteFile(List<String> fileIds) {
		return null;
	}

	@Override
	public String fileContent(String fileId) {
		return "";
	}

}
