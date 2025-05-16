package com.hula.ai.gpt.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.model.dto.ModelDTO;
import com.hula.ai.client.service.GptService;
import com.hula.ai.common.constant.AiConstants;
import com.hula.ai.common.enums.StatusEnum;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.config.dto.AppInfoDTO;
import com.hula.ai.framework.validator.base.BaseAssert;
import com.hula.ai.gpt.mapper.AssistantMapper;
import com.hula.ai.gpt.mapper.ModelMapper;
import com.hula.ai.gpt.pojo.entity.Assistant;
import com.hula.ai.gpt.pojo.vo.ChatVO;
import com.hula.ai.gpt.service.IChatMessageService;
import com.hula.ai.gpt.service.IChatService;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.vo.resp.user.UserInfoResp;
import com.hula.core.user.service.ConfigService;
import com.hula.core.user.service.UserService;
import com.hula.exception.BizException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * chatgpt接口实现类
 *
 * @author: 云裂痕
 * @date: 2025/03/07
 * 得其道 乾乾
 */
@Slf4j
@Service
public class GptServiceImpl implements GptService {
    @Autowired
    private IChatService chatService;
    @Autowired
    private IChatMessageService chatMessageService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AssistantMapper assistantMapper;
    @Autowired
    private ConfigService configService;
	@Resource
	private UserService userService;

    @Override
    public ModelDTO getModel(String model) {
		return DozerUtil.convertor(modelMapper.getModel(model), ModelDTO.class);
    }

    @Override
    public Long saveChat(ChatCommand command) {
        return chatService.saveChat(command);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String chatMessage(ChatCommand command) {
        return chatMessageService.saveChatMessage(command);
    }

    @Override
    public ChatMessageDTO getMessageByConverstationId(String conversationId) {
		return chatMessageService.getChatByMessageId(conversationId);
    }

    @Override
    public List<ChatMessageDTO> listMessageByConverstationId(Long uid, String conversationId) {
		UserInfoResp user = userService.getUserInfo(uid);
		Boolean context = user.getContext();
        ChatMessageDTO chatMessage = chatMessageService.getChatByMessageId(conversationId);
        List<ChatMessageDTO> chatMessages = new ArrayList<>();
        if (ObjectUtil.isNotNull(context) && context) {
            chatMessages = chatMessageService.listChatMessage(chatMessage.getChatId());
        }
        chatMessages.add(chatMessage);
        ChatVO chat = chatService.getChatById(chatMessage.getChatId());
        if (ObjectUtil.isNotNull(chat) && ObjectUtil.isNotNull(chat.getAssistantId())) {
            Assistant assistant = assistantMapper.selectById(chat.getAssistantId());
            if (ObjectUtil.isNotNull(assistant) && ObjectUtil.isNotNull(assistant.getSystemPrompt())) {
                chatMessages.add(0, ChatMessageDTO.builder().role(ChatRoleEnum.SYSTEM.getValue()).content(assistant.getSystemPrompt()).build());
            }
        }
        return chatMessages;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChatMessage(ChatMessageCommand command) {
        int response = chatMessageService.saveChatMessage(command);
        if (response <= 0) {
            return;
        }
        if (!ChatRoleEnum.ASSISTANT.getValue().equals(command.getRole())) {
            return;
        }
        if (!ChatStatusEnum.SUCCESS.getValue().equals(command.getStatus())) {
            return;
        }

		// 扣除用户电量
		ChatVO chat = chatService.getChatById(command.getChatId());
       	userService.subElectricity(chat.getUid());
    }

    @Override
    public List<ChatMessageDTO> saveChatMessage(ChatCommand command, Long chatId, String messageId) {
        return chatMessageService.saveChatMessage(command, chatId, messageId);
    }

    /**
     * 校验对话数据
     */
    @Override
    public ChatCommand validateGptCommand(ChatCommand command) {
        BaseAssert.isBlankOrNull(command.getModel(), "缺少模型信息");
        BaseAssert.isBlankOrNull(command.getPrompt(), "缺少prompt");
        ModelDTO model = getModel(command.getModel());
        if (ObjectUtil.isNull(model)) {
            throw new BizException("模型已经不存在啦，请切换模型进行回复～");
        }
        if (StatusEnum.DISABLED.getValue().equals(model.getStatus())) {
            throw new BizException("该模型已被禁用，请切换模型进行回复～");
        }
        ChatModelEnum modelEnum = ChatModelEnum.getEnum(command.getModel());
        if (ObjectUtil.isNull(modelEnum)) {
            throw new BizException("未知的模型类型，功能未接入");
        }
        // 兼容老版本，没有选择版本时使用模型默认版本
        if (ObjectUtil.isNull(command.getModelVersion())) {
            ModelDTO modelDTO = getModel(command.getModel());
            command.setModelVersion(modelDTO.getVersion());
        }
        String messageId = UUID.fastUUID().toString();
        command.setConversationId(messageId);
        //校验用户
        if (ObjectUtil.isNull(command.getApi()) || !command.getApi()) {
            validateUser(command);
        }
        return command;
    }

    /**
     * 校验账户
     *
     * @param command
     */
    private void validateUser(ChatCommand command) {
		User user = userService.getUserById(command.getUid());
		if (ObjectUtil.isNull(user)) {
			throw new BizException("用户不存在!");
		}
        AppInfoDTO appInfo = configService.getBeanByName(AiConstants.APP_INFO, AppInfoDTO.class);
		if (ObjectUtil.isNotNull(appInfo) && ObjectUtil.isNotNull(appInfo.getIsGPTLimit()) && appInfo.getIsGPTLimit().equals(StatusEnum.ENABLED.getValue())) {
            return;
        }
        if (user.getNum() < 1) {
            throw new BizException("电量不足，请联系管理员!");
        }
    }

    @Override
    public void updateMessageStatus(String messageId, Integer status) {
        chatMessageService.updateMessageStatus(messageId, status);
    }

    @Override
    public void updateMessageUsedTokens(String messageId, Long usedTokens) {
        chatMessageService.updateMessageUsedTokens(messageId, usedTokens);
    }

}
