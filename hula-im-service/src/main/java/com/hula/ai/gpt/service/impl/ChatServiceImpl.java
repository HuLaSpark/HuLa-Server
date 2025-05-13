package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.common.utils.SnowFlakeUtil;
import com.hula.ai.framework.validator.base.BaseAssert;
import com.hula.ai.gpt.mapper.AssistantMapper;
import com.hula.ai.gpt.mapper.ChatMapper;
import com.hula.ai.gpt.pojo.entity.Assistant;
import com.hula.ai.gpt.pojo.entity.Chat;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatVO;
import com.hula.ai.gpt.service.IChatService;
import com.hula.core.user.service.UserService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天摘要 服务实现类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service("ChatAiMessage")
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements IChatService {
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private AssistantMapper assistantMapper;
	@Autowired
	private UserService userService;

    /**
     * 根据id获取聊天摘要信息
     *
     * @param id 聊天摘要id
     * @return
     */
    private Chat getChat(Long id) {
        Chat chat = chatMapper.selectById(id);
        if (ObjectUtil.isNull(chat)) {
            throw new BizException("聊天摘要信息不存在，无法操作");
        }
        return chat;
    }

    @Override
    public IPage<ChatVO> pageChat(ChatParam param) {
		return chatMapper.pageChat(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<ChatVO> listChat(ChatParam param) {
		return chatMapper.listChat(param);
    }

    @Override
    public ChatVO getChatById(Long id) {
        return DozerUtil.convertor(getChat(id), ChatVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveChat(ChatCommand command) {
        String chatNumber = command.getChatNumber();
        Chat chat;
        if (ObjectUtil.isNull(command.getChatNumber())) {
            chat = Chat.builder().chatNumber(String.valueOf(SnowFlakeUtil.snowflakeId())).assistantId(command.getAssistantId())
                    .title(command.getPrompt()).uid(command.getUid()).build();
            chatMapper.insert(chat);
            return chat.getId();
        }
        chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>().eq(Chat::getChatNumber, chatNumber));
        if (ObjectUtil.isNull(chat)) {
            chat = Chat.builder().chatNumber(chatNumber).assistantId(command.getAssistantId())
                    .title(command.getPrompt()).uid(command.getUid()).build();
            chatMapper.insert(chat);
        }
        return chat.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatVO saveSSEChat(ChatCommand command) {
        Chat chat = DozerUtil.convertor(command, Chat.class);
        chat.setCreatedBy(command.getOperater());
        chat.setChatNumber(String.valueOf(SnowFlakeUtil.snowflakeId()));
        if (ObjectUtil.isNotNull(command.getAssistantId())) {
            Assistant assistant = assistantMapper.selectById(command.getAssistantId());
            command.setPrompt(assistant.getSystemPrompt());
        }
        BaseAssert.isBlankOrNull(command.getPrompt(), "请输入提示词");
        int maxLength = 30;
        chat.setTitle(command.getPrompt().substring(0, command.getPrompt().length() > maxLength ? maxLength : command.getPrompt().length()));
		chat.setUid(command.getUid());
        chat.setCreatedBy(command.getUid());
        chat.setCreatedTime(LocalDateTime.now());
        chatMapper.insert(chat);
        return ChatVO.builder().chatNumber(chat.getChatNumber()).prompt(command.getPrompt()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeChatByChatNumber(String chatNumber) {
		return chatMapper.delete(new LambdaQueryWrapper<Chat>().eq(Chat::getChatNumber, chatNumber));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeChatByIds(List<Long> ids) {
		return chatMapper.deleteBatchIds(ids);
    }

}
