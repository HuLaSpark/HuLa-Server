package com.hula.ai.llm.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.service.ModelService;
import com.hula.ai.llm.moonshot.MoonshotClient;
import com.hula.ai.llm.moonshot.constant.ModelConstant;
import com.hula.ai.llm.moonshot.entity.request.ChatCompletion;
import com.hula.ai.llm.moonshot.entity.request.ChatCompletionMessage;
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
 * 月之暗面 接口实现
 *
 * @author: 云裂痕
 * @date: 2024/5/30
 * @version: 1.1.7
 * 得其道
 * 乾乾
 */
@Service
public class MoonshotServiceImpl implements ModelService {
    private static MoonshotClient moonshotClient;

    @Autowired
    public MoonshotServiceImpl(MoonshotClient moonshotClient) {
        MoonshotServiceImpl.moonshotClient = moonshotClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        return null;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
							  Long chatId, String conversationId, String prompt, String version, Long uid) {
        if (ObjectUtil.isNull(moonshotClient.getApiKey())) {
            throw new BizException("未加载到密钥信息");
        }
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().filter(d -> !d.getRole().equals(ChatRoleEnum.SYSTEM.getValue())).forEach(v -> messages.add(new ChatCompletionMessage(v.getRole(), v.getContent())));
        String modelVaersion = ObjectUtil.isNotNull(version) ? version : ModelConstant.API_MODEL_8K;
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        return moonshotClient.streamChat(response, chatCompletion, chatId, conversationId, modelVaersion, uid.toString(), isWs);
    }

	/**
	 * 文件列表
	 */
	@Override
	public JSONArray fileList() {
		return moonshotClient.filesList();
	}

	@Override
	public Boolean deleteFile(List<String> fileIds) {
		int num = 0;
		for (String fileId : fileIds) {
			num = moonshotClient.delfile(fileId)? ++num: num;
		}
		return num > 0;
	}

	/**
	 * 上传文件
	 */
	@Override
	public FileUploadResponse uploadFile(MultipartFile file) {
		return moonshotClient.uploadFile(file);
	}


	/**
	 * 查看文件内容
	 * @param fileId 文件id
	 */
	public String fileContent(String fileId) {
		return moonshotClient.fileContent(fileId);
	}

}
