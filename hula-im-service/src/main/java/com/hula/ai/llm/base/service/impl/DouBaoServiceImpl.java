package com.hula.ai.llm.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.doubao.DouBaoClient;
import com.hula.ai.llm.doubao.enums.ModelEnum;
import com.hula.exception.BizException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 豆包 接口实现
 *
 * @author: 云裂痕
 * @date: 2025/03/13
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Service
public class DouBaoServiceImpl implements ModelService {
    private static DouBaoClient douBaoClient;

    @Autowired
    public DouBaoServiceImpl(DouBaoClient douBaoClient) {
        DouBaoServiceImpl.douBaoClient = douBaoClient;
    }

    /**
     * 豆包文本对话
     * https://www.volcengine.com/docs/82379/1298454
     *
     * @param chatId
     * @param chatMessages
     * @return
     */
    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ObjectUtil.isNull(douBaoClient)) {
            throw new BizException("未加载到豆包密钥信息");
        }
        List<ChatMessage> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            messages.add(ChatMessage.builder().role(ChatMessageRole.valueOf(v.getRole().toUpperCase())).content(v.getContent()).build());
        });
        String modelVaersion = ObjectUtil.isNotNull(version) ? version : ModelEnum.LITE.getModel();
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        ChatCompletionResult response = douBaoClient.chat(request);
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getId())
                .model(ChatModelEnum.DOUBAO.getValue()).modelVersion(modelVaersion)
                .content(response.getChoices().get(0).getMessage().getContent().toString()).role(ChatRoleEnum.ASSISTANT.getValue())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(douBaoClient.getApiKey()).usedTokens(Long.valueOf(response.getUsage().getTotalTokens()))
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (ObjectUtil.isNull(douBaoClient.getApiKey())) {
            throw new BizException("未加载到豆包密钥信息");
        }
        List<ChatMessage> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            messages.add(ChatMessage.builder().role(ChatMessageRole.valueOf(v.getRole().toUpperCase())).content(v.getContent()).build());
        });
        String modelVaersion = ObjectUtil.isNotNull(version) ? version : ModelEnum.LITE.getModel();
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        Boolean flag = douBaoClient.streamChat(response, chatCompletionRequest, chatId, conversationId, modelVaersion, uid.toString(), isWs);
        if (isWs) {
            return false;
        }
        return flag;
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
