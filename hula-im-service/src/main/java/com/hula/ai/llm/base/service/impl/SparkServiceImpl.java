package com.hula.ai.llm.base.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
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
import com.hula.ai.llm.spark.SparkClient;
import com.hula.ai.llm.spark.entity.request.ChatCompletionMessage;
import com.hula.ai.llm.spark.entity.request.ChatRequest;
import com.hula.ai.llm.spark.entity.response.ChatSyncResponse;
import com.hula.ai.llm.spark.entity.response.Usage;
import com.hula.ai.llm.spark.enums.ModelEnum;
import com.hula.ai.llm.spark.listener.SSEListener;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 讯飞星火 接口实现
 *
 * @author: 云裂痕
 * @date: 2025/03/13
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Service
public class SparkServiceImpl implements ModelService {
    private static SparkClient sparkClient;

    public SparkServiceImpl(SparkClient SparkClient) {
        SparkServiceImpl.sparkClient = SparkClient;
    }

    @Override
    @SneakyThrows
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ObjectUtil.isNull(sparkClient)) {
            throw new BizException("讯飞星火无有效token，请切换其他模型进行聊天");
        }
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            ChatCompletionMessage currentMessage = new ChatCompletionMessage(v.getRole(), v.getContent());
            messages.add(currentMessage);
        });
        // 构造请求
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传,取值为[1,4096],默认为2048
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.5)
                // 指定请求版本，默认使用最新2.0版本
                .apiVersion(ObjectUtil.isNotNull(version) ? ModelEnum.getEnum(version) : ModelEnum.Lite)
                .chatId(chatId.toString())
                .build();
        ChatSyncResponse response = sparkClient.chat(chatRequest);
        if (!response.isSuccess()) {
            throw new LLMException(response.getErrTxt());
        }
        Usage textUsage = response.getTextUsage();
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(UUID.fastUUID().toString())
                .model(ChatModelEnum.SPARK.getValue()).modelVersion(ModelEnum.Lite.getVersion())
                .content(response.getContent()).role(ChatRoleEnum.ASSISTANT.getValue())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(sparkClient.apiKey).usedTokens(Long.valueOf(textUsage.getTotalTokens()))
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (ObjectUtil.isNull(sparkClient.appid)) {
            throw new BizException("未加载到密钥信息");
        }
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            ChatCompletionMessage currentMessage = new ChatCompletionMessage(v.getRole(), v.getContent());
            messages.add(currentMessage);
        });
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传,取值为[1,4096],默认为2048
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.5)
                // 指定请求版本，默认使用最新2.0版本
                .apiVersion(ObjectUtil.isNotNull(version) ? ModelEnum.getEnum(version) : ModelEnum.Lite)
                .chatId(chatId.toString())
                .build();
        chatRequest.getHeader().setAppId(sparkClient.appid);
//        chatRequest.getHeader().setUid(uid);
        SSEListener sseListener = new SSEListener(chatRequest, response, chatId, conversationId, uid.toString(), version, isWs);
        sparkClient.streamChat(chatRequest, sseListener);
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
