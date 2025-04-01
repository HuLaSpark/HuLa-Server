package com.hula.ai.llm.base.service;

import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.framework.util.file.FileUploadResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 大模型接口
 *
 * @author: 云裂痕
 * @date: 2024/5/30
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface ModelService {

    /**
     * 同步响应
     *
     * @param chatId
     * @param version
     * @param chatMessages
     * @return
     */
    ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version);

    /**
     * 流式响应
     *
     * @return true 响应异常 false 响应正常
     */
    Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
					   Long chatId, String conversationId, String prompt, String version, Long uid);

	/**
	 * 上传文件
	 */
	FileUploadResponse uploadFile(MultipartFile file);

	/**
	 * 文件列表
	 */
	JSONArray fileList();

	/**
	 * 删除文件
	 */
	Boolean deleteFile(List<String> fileIds);

	/**
	 * 查看文件内容
	 * @param fileId 文件id
	 */
	String fileContent(String fileId);
}
