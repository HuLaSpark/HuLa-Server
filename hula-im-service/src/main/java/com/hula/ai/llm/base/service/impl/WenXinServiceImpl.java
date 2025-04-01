package com.hula.ai.llm.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatContentEnum;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.service.GptService;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.entity.ChatData;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.wenxin.WenXinClient;
import com.hula.ai.llm.wenxin.entity.request.ChatCompletionMessage;
import com.hula.ai.llm.wenxin.entity.request.ImagesBody;
import com.hula.ai.llm.wenxin.entity.response.ChatResponse;
import com.hula.ai.llm.wenxin.entity.response.ImageResponse;
import com.hula.ai.llm.wenxin.enums.ModelEnum;
import com.hula.ai.llm.wenxin.listener.SSEListener;
import com.hula.domain.vo.res.ApiResult;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文心一言 接口实现
 *
 * @author: 云裂痕
 * @date: 2025/03/08
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Service
public class WenXinServiceImpl implements ModelService {
    private static WenXinClient wenXinClient;
    private static GptService gptService;

    @Autowired
    public WenXinServiceImpl(GptService gptService, WenXinClient wenXinClient) {
        WenXinServiceImpl.gptService = gptService;
        WenXinServiceImpl.wenXinClient = wenXinClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ObjectUtil.isNull(wenXinClient)) {
            throw new BizException("文心一言无有效token，请切换其他模型进行聊天");
        }
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().filter(d -> !d.getRole().equals(ChatRoleEnum.SYSTEM.getValue())).forEach(v -> {
            messages.add(ChatCompletionMessage.builder().role(v.getRole()).content(v.getContent()).build());
        });
        ModelEnum modelEnum = ModelEnum.ERNIE_Bot_turbo;
        if (ObjectUtil.isNotNull(version)) {
            modelEnum = ModelEnum.getEnum(version);
        }
        if (ObjectUtil.isNull(modelEnum)) {
            throw new BizException("未知的模型");
        }
        ApiResult<ChatResponse> wenxinResponse = wenXinClient.chat(messages, modelEnum);
        if (!wenxinResponse.getSuccess()) {
            throw new LLMException(wenxinResponse.getMsg());
        }
        ChatResponse response = wenxinResponse.getData();
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getId())
                .model(ChatModelEnum.WENXIN.getValue()).modelVersion(modelEnum.getLabel())
                .content(response.getResult()).role(ChatRoleEnum.ASSISTANT.getValue())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(wenXinClient.getApiKey()).usedTokens(response.getUsage().getTotalTokens())
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (ObjectUtil.isNull(wenXinClient.getApiKey())) {
            throw new BizException("未加载到密钥信息");
        }
        if (isDraw) {
            return image(sseEmitter, response, chatId, conversationId, prompt);
        }
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().filter(d -> !d.getRole().equals(ChatRoleEnum.SYSTEM.getValue())).forEach(v -> {
            messages.add(ChatCompletionMessage.builder().role(v.getRole()).content(v.getContent()).build());
        });
        SSEListener sseListener = new SSEListener(response, sseEmitter, chatId, conversationId, ChatModelEnum.WENXIN.getValue(), version, uid, isWs);
        ModelEnum model = ObjectUtil.isNotNull(version) ? ModelEnum.getEnum(version) : ModelEnum.ERNIE_Bot_turbo;
        if (ObjectUtil.isNull(model)) {
            throw new BizException("文心大模型不存在，请检查模型名称。");
        }
        wenXinClient.streamChat(messages, sseListener, model);
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

	/**
     * 文生图
     *
     * @param prompt
     * @return
     */
    @SneakyThrows
    private Boolean image(SseEmitter sseEmitter, HttpServletResponse response, Long chatId, String conversationId, String prompt) {
        // 通知客户端返回为图片
        ChatData chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
                .parentMessageId(conversationId)
                .role(ChatRoleEnum.ASSISTANT.getValue()).contentType(ChatContentEnum.IMAGE.getValue()).build();
        response.getWriter().write(JSON.toJSONString(chatData));
        response.getWriter().flush();

        // 请求生成图片 并返回base64信息
        ImagesBody body = ImagesBody.builder().prompt(prompt).build();
		ApiResult<ImageResponse> imageResponse = wenXinClient.image(body);
        if (!imageResponse.getSuccess()) {
            throw new BizException(imageResponse.getMsg());
        }
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        ImageResponse image = imageResponse.getData();
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(image.getId()).parentMessageId(conversationId)
                .model(ChatModelEnum.WENXIN.getValue()).modelVersion(ModelEnum.STABLE_DIFFUSION_XL.getLabel())
                .content(JSON.toJSONString(image.getData())).contentType(ChatContentEnum.IMAGE.getValue()).role(ChatRoleEnum.ASSISTANT.getValue())
                .status(ChatStatusEnum.SUCCESS.getValue()).usedTokens(image.getUsage().getTotalTokens())
                .build();
        gptService.saveChatMessage(chatMessage);
        chatData.setContent(image.getData());
        response.getWriter().write("\n" + JSON.toJSONString(chatData));
        response.getWriter().flush();
        return false;
    }

}
