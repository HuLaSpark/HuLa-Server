package com.hula.ai.client.service;

import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.model.dto.ModelDTO;

import java.util.List;

/**
 * gpt服务
 *
 * @author: 云裂痕
 * @date: 2024/5/29
 * @version: 1.0.0
 * Copyright Ⓒ 2024 Master Computer Corporation Limited All rights reserved.
 */
public interface GptService {

    /**
     * 新增聊天摘要
     *
     * @param command 聊天摘要
     * @return 结果
     */
    Long saveChat(ChatCommand command);

    /**
     * 提问
     *
     * @param command 提问内容
     * @return 返回json
     */
    String chatMessage(ChatCommand command);

    /**
     * 根据对话id 获取对话内容
     *
     * @param conversationId
     * @return
     */
    ChatMessageDTO getMessageByConverstationId(String conversationId);

    /**
     * 根据对话id 获取上下文内容 最多10条
     *
     * @param uid        操作人
     * @param conversationId 对话id
     * @return
     */
    List<ChatMessageDTO> listMessageByConverstationId(Long uid, String conversationId);

    /**
     * 新增回复消息（流式输出使用）
     *
     * @param command 对话消息
     * @return 结果
     */
    void saveChatMessage(ChatMessageCommand command);

    /**
     * 新增对话消息（json使用）
     *
     * @param command 对话消息
     * @return 结果
     */
    List<ChatMessageDTO> saveChatMessage(ChatCommand command, Long chatId, String messageId);

    /**
     * 校验用户额度
     *
     * @param command
     * @return
     */
    ChatCommand validateGptCommand(ChatCommand command);

    /**
     * 修改对话状态
     *
     * @param messageId 消息id
     * @param status    状态
     * @return 结果
     */
    void updateMessageStatus(String messageId, Integer status);

    /**
     * 更新对话使用token数
     *
     * @param messageId  消息id
     * @param usedTokens 使用token数
     * @return
     */
    void updateMessageUsedTokens(String messageId, Long usedTokens);

    /**
     * 获取模型信息
     *
     * @param model 模型名称
     * @return
     */
    ModelDTO getModel(String model);

}
