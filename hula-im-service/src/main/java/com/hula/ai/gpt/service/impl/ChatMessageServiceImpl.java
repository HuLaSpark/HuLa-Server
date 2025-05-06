package com.hula.ai.gpt.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.client.enums.ChatContentEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.common.enums.IntegerEnum;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.AssistantMapper;
import com.hula.ai.gpt.mapper.ChatMapper;
import com.hula.ai.gpt.mapper.ChatMessageMapper;
import com.hula.ai.gpt.pojo.entity.Assistant;
import com.hula.ai.gpt.pojo.entity.Chat;
import com.hula.ai.gpt.pojo.entity.ChatMessage;
import com.hula.ai.gpt.pojo.param.ChatMessageParam;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatMessageVO;
import com.hula.ai.gpt.service.IChatMessageService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话消息 服务实现类
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private AssistantMapper assistantMapper;

    /**
     * 根据id获取对话消息信息
     *
     * @param id 对话消息id
     * @return
     */
    private ChatMessage getChatMessage(Long id) {
        ChatMessage chatMessage = chatMessageMapper.selectById(id);
        if (ObjectUtil.isNull(chatMessage)) {
            throw new BizException("对话消息信息不存在，无法操作");
        }
        return chatMessage;
    }

    /**
     * 根据id获取对话消息信息
     *
     * @param messageId 对话消息id
     * @return
     */
    private ChatMessage getChatMessageByMessageId(String messageId) {
        ChatMessage chatMessage = chatMessageMapper.selectOne(new LambdaQueryWrapper<ChatMessage>().eq(ChatMessage::getMessageId, messageId));
        if (ObjectUtil.isNull(chatMessage)) {
            throw new BizException("对话消息信息不存在，无法操作");
        }
        return chatMessage;
    }

    @Override
    public IPage<ChatMessageVO> pageChatMessage(ChatMessageParam param) {
		return chatMessageMapper.pageChatMessage(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<ChatMessageVO> listChatMessage(ChatParam param) {
		return chatMessageMapper.listChatMessage(param);
    }

    @Override
    public List<ChatMessageDTO> listChatMessage(Long chatId) {
        List<ChatMessage> chatMessages = chatMessageMapper.selectList(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getChatId, chatId).eq(ChatMessage::getStatus, IntegerEnum.TWO.getValue())
                .orderByDesc(ChatMessage::getId).last("limit 10"));
        chatMessages = chatMessages.stream().sorted(Comparator.comparing(ChatMessage::getId)).collect(Collectors.toList());
        return DozerUtil.convertor(chatMessages, ChatMessageDTO.class);
    }

    @Override
    public ChatMessageVO getChatMessageById(Long id) {
        return DozerUtil.convertor(getChatMessage(id), ChatMessageVO.class);
    }

    @Override
    public Long getChatIdByMessageId(String messageId) {
        ChatMessage chatMessage = getChatMessageByMessageId(messageId);
        return chatMessage.getChatId();
    }

    @Override
    public ChatMessageDTO getChatByMessageId(String messageId) {
        ChatMessage chatMessage = getChatMessageByMessageId(messageId);
        return DozerUtil.convertor(chatMessage, ChatMessageDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveChatMessage(ChatCommand command) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>().eq(Chat::getChatNumber, command.getChatNumber()));
        if (ObjectUtil.isNull(chat)) {
            throw new BizException("对话不存在，请刷新页面");
        }
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chat.getId()).messageId(command.getConversationId()).model(command.getModel()).modelVersion(command.getModelVersion())
                .content(command.getPrompt()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.USER.getValue()).status(ChatStatusEnum.REPLY.getValue())
                .build();
        chatMessage.setCreatedBy(command.getOperater());
        chatMessage.setCreatedTime(LocalDateTime.now());
        chatMessageMapper.insert(chatMessage);
        return chatMessage.getMessageId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveChatMessage(ChatMessageCommand command) {
		return chatMessageMapper.insert(DozerUtil.convertor(command, ChatMessage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ChatMessageDTO> saveChatMessage(ChatCommand command, Long chatId, String messageId) {
        List<ChatMessageDTO> messages = listChatMessage(chatId);
        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chatId).messageId(UUID.fastUUID().toString())
                .model(command.getModel()).modelVersion(command.getModelVersion())
                .content(command.getPrompt()).role(ChatRoleEnum.USER.getValue())
                .status(ChatStatusEnum.REPLY.getValue())
                .build();
		chatMessage.setCreatedBy(command.getOperater());
        chatMessageMapper.insert(chatMessage);
        messages.add(DozerUtil.convertor(chatMessage, ChatMessageDTO.class));
        if (ObjectUtil.isNotNull(command.getAssistantId())) {
            Assistant assistant = assistantMapper.selectById(command.getAssistantId());
            if (ObjectUtil.isNotNull(assistant.getSystemPrompt())) {
                messages.add(0, ChatMessageDTO.builder().role(ChatRoleEnum.SYSTEM.getValue()).content(assistant.getSystemPrompt()).build());
            }
        } else if (ObjectUtil.isNotNull(command.getSystemPrompt())) {
            messages.add(0, ChatMessageDTO.builder().role(ChatRoleEnum.SYSTEM.getValue()).content(command.getSystemPrompt()).build());
        }
        return messages;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMessageStatus(String messageId, Integer status) {
        chatMessageMapper.updateMessageStatus(messageId, status);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMessageUsedTokens(String messageId, Long usedTokens) {
		chatMessageMapper.updateMessageUsedTokens(messageId, usedTokens);
		return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeChatMessageByIds(List<Long> ids) {
        return chatMessageMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeChatMessageByMessageId(String messageId) {
        ChatMessage chatMessage = getChatMessageByMessageId(messageId);
        chatMessageMapper.delete(new LambdaQueryWrapper<ChatMessage>().eq(ChatMessage::getMessageId, messageId));
        return chatMessageMapper.delete(new LambdaQueryWrapper<ChatMessage>().eq(ChatMessage::getMessageId, chatMessage.getParentMessageId()));
    }

}
