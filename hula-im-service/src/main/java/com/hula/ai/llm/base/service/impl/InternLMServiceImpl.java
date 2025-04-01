package com.hula.ai.llm.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.internlm.InternlmClient;
import com.hula.ai.llm.internlm.constant.ModelConstant;
import com.hula.ai.llm.internlm.entity.request.ChatCompletion;
import com.hula.ai.llm.internlm.entity.request.ChatCompletionMessage;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 书生浦语 接口实现
 *
 * @author: 云裂痕
 * @date: 2025/03/13
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Service
public class InternLMServiceImpl implements ModelService {
    private static InternlmClient internlmClient;

    @Autowired
    public InternLMServiceImpl(InternlmClient internlmClient) {
        InternLMServiceImpl.internlmClient = internlmClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        return null;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (ObjectUtil.isNull(internlmClient.getToken())) {
            throw new BizException("未加载到密钥信息");
        }
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().filter(d -> !d.getRole().equals(ChatRoleEnum.SYSTEM.getValue())).forEach(v -> {
            messages.add(new ChatCompletionMessage(v.getRole(), v.getContent()));
        });
        String modelVaersion = ObjectUtil.isNotNull(version) ? version : ModelConstant.LATEST;
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        return internlmClient.streamChat(response, chatCompletion, chatId, conversationId, modelVaersion, uid.toString(), isWs);
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
