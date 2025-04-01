package com.hula.ai.llm.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.tongyi.TongYiClient;
import com.hula.ai.llm.tongyi.listener.SSEListener;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 通义 接口实现
 * 文档地址 https://help.aliyun.com/zh/dashscope/developer-reference/api-details?spm=a2c4g.11186623.0.0.234374fakjtImN
 *
 * @author: 云裂痕
 * @date: 2025/03/13
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Service
public class TongYiServiceImpl implements ModelService {
    private static TongYiClient tongYiClient;

    @Autowired
    public TongYiServiceImpl(TongYiClient tongYiClient) {
        TongYiServiceImpl.tongYiClient = tongYiClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        Generation gen = new Generation();
        MessageManager msgManager = new MessageManager(20);
        chatMessages.stream().forEach(v -> {
            msgManager.add(Message.builder().role(v.getRole()).content(v.getContent()).build());
        });
        QwenParam param = QwenParam.builder().apiKey(tongYiClient.getAppKey())
                .model(ObjectUtil.isNotNull(version) ? version : Generation.Models.QWEN_TURBO)
                .messages(msgManager.get())
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.3)
                .enableSearch(true)
                .build();
        try {
            GenerationResult response = gen.call(param);
            List<GenerationOutput.Choice> choices = response.getOutput().getChoices();
            ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getRequestId())
                    .model(ChatModelEnum.TONGYI.getValue()).modelVersion(param.getModel())
                    .content(choices.get(0).getMessage().getContent()).finishReason(choices.get(0).getFinishReason())
                    .role(ChatRoleEnum.ASSISTANT.getValue()).status(ChatStatusEnum.SUCCESS.getValue())
                    .appKey(param.getApiKey()).usedTokens(Long.valueOf(response.getUsage().getInputTokens() + response.getUsage().getOutputTokens()))
                    .response(JSON.toJSONString(response))
                    .build();
            return chatMessage;
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            e.printStackTrace();
            throw new LLMException(e.getMessage());
        }
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (ObjectUtil.isNull(tongYiClient.getAppKey())) {
            throw new BizException("未加载到密钥信息");
        }
        MessageManager msgManager = new MessageManager(20);
        chatMessages.stream().forEach(v -> {
            msgManager.add(Message.builder().role(v.getRole()).content(v.getContent()).build());
        });
        Generation gen = new Generation();
        QwenParam param = QwenParam.builder().apiKey(tongYiClient.getAppKey())
                .model(ObjectUtil.isNotNull(version) ? version : Generation.Models.QWEN_TURBO)
                .messages(msgManager.get())
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .enableSearch(true)
                .build();
        Semaphore semaphore = new Semaphore(0);
        SSEListener sseListener = new SSEListener(response, semaphore, chatId, conversationId, uid.toString(), version, isWs);
        gen.streamCall(param, sseListener);
        if (isWs) {
            return false;
        }
        semaphore.acquire();
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
