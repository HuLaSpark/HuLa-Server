package com.hula.ai.llm.base.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.model.dto.ModelDTO;
import com.hula.ai.client.service.GptService;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.locallm.coze.CozeClient;
import com.hula.ai.llm.locallm.enums.ModelTypeEnum;
import com.hula.ai.llm.locallm.gitee.GiteeClient;
import com.hula.ai.llm.locallm.langchain.LangchainClient;
import com.hula.ai.llm.locallm.ollama.OllamaClient;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 本地模型 接口实现
 *
 * @author: 云裂痕
 * @date: 2025/03/13
 * @version: 1.2.8
 * 乾乾
 */
@Service
public class LocalLMServiceImpl implements ModelService {
    private static LangchainClient langchainClient;
    private static OllamaClient ollamaClient;
    private static CozeClient cozeClient;
    private static GptService gptService;
    private static GiteeClient giteeClient;

    @Autowired
    public LocalLMServiceImpl(LangchainClient langchainClient, OllamaClient ollamaClient, CozeClient cozeClient, GptService gptService, GiteeClient giteeClient) {
        LocalLMServiceImpl.langchainClient = langchainClient;
        LocalLMServiceImpl.ollamaClient = ollamaClient;
        LocalLMServiceImpl.cozeClient = cozeClient;
        LocalLMServiceImpl.gptService = gptService;
        LocalLMServiceImpl.giteeClient = giteeClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        return null;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        ModelDTO modelDTO = gptService.getModel(ChatModelEnum.LOCALLM.getValue());
        ModelTypeEnum modelType = ModelTypeEnum.getEnum(modelDTO.getLocalModelType());
        switch (modelType) {
            case LANGCHAIN:
                return langchainClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid.toString(), chatMessages, prompt, version, modelDTO);
            case OLLAMA:
                return ollamaClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid.toString(), chatMessages, prompt, version, modelDTO);
            case COZE:
                return cozeClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid.toString(), chatMessages, prompt, version, modelDTO);
            case GITEE_AI:
                return giteeClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid.toString(), chatMessages, prompt, version, modelDTO);
            default:
                throw new BizException("未知的模型类型，功能未接入");
        }
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
