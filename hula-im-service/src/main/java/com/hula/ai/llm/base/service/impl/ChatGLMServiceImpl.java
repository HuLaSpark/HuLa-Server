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
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.chatglm.ChatGLMClient;
import com.hula.exception.BizException;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 智谱清言 接口实现
 *
 * @author: 云裂痕
 * @date: 2024/5/30
 * @version: 1.1.7
 * 得其道
 * 乾乾
 */
@Service
public class ChatGLMServiceImpl implements ModelService {
    private static ChatGLMClient chatGLMClient;

    @Autowired
    public ChatGLMServiceImpl(ChatGLMClient chatGLMClient) {
        ChatGLMServiceImpl.chatGLMClient = chatGLMClient;
    }

    /**
     * 智谱清言模型聊天
     * 支持超拟人大模型，将modelId替换为characterglm
     * query中增加参数{meta：{"user_info": "我是陆星辰","bot_info:"苏梦远，本名苏远心，是一位当红的国内女歌手及演员。", "user_name": "陆星辰", "bot_name": "苏梦远"}}
     *
     * @param chatId
     * @param chatMessages
     * @return
     */
    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ObjectUtil.isNull(chatGLMClient)) {
            throw new BizException("智谱清言无有效token，请切换其他模型进行聊天");
        }
        List<ChatMessage> messages = new ArrayList<>();
        chatMessages.stream().filter(d -> !d.getRole().equals(ChatRoleEnum.SYSTEM.getValue())).forEach(v -> {
            messages.add(new ChatMessage(v.getRole(), v.getContent()));
        });
        String modelVaersion = ObjectUtil.isNotNull(version) ? version : Constants.ModelChatGLM3TURBO;
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        // 关闭搜索示例
        //  modelApiRequest.setRef(new HashMap<String, Object>(){{
        //    put("enable",false);
        // }});
        // 开启搜索示例
        // modelApiRequest.setRef(new HashMap<String, Object>(){{
        //    put("enable",true);
        //    put("search_query","历史");
        //  }});
        ModelApiResponse response = chatGLMClient.chat(request);
        if (!response.isSuccess()) {
            throw new LLMException(response.getMsg());
        }
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getData().getTaskId())
                .model(ChatModelEnum.CHATGLM.getValue()).modelVersion(modelVaersion)
                .content(response.getData().getChoices().get(0).getDelta().getContent()).role(ChatRoleEnum.ASSISTANT.getValue())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(chatGLMClient.getAppKey()).usedTokens(Long.valueOf(response.getData().getUsage().getTotalTokens()))
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (ObjectUtil.isNull(chatGLMClient.getAppKey())) {
            throw new BizException("未加载到密钥信息");
        }
        List<ChatMessage> messages = new ArrayList<>();
        chatMessages.stream().filter(d -> !d.getRole().equals(ChatRoleEnum.SYSTEM.getValue())).forEach(v -> {
            messages.add(new ChatMessage(v.getRole(), v.getContent()));
        });
        String modelVaersion = ObjectUtil.isNotNull(version) ? version : Constants.ModelChatGLM3TURBO;
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelVaersion)
                .stream(Boolean.TRUE)
                .messages(messages)
                .build();
        Boolean flag = chatGLMClient.streamChat(response, chatCompletionRequest, chatId, conversationId, modelVaersion, uid.toString(), isWs);
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
