package com.luohuo.flex.ai.service.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiChatConversationCreateMyReqVO;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiDelReqVO;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiChatConversationPageReqVO;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiChatConversationUpdateMyReqVO;
import com.luohuo.flex.ai.dal.chat.AiChatConversationDO;
import com.luohuo.flex.ai.dal.model.AiChatRoleDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiModelTypeEnum;
import com.luohuo.flex.ai.mapper.chat.AiChatConversationMapper;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeService;
import com.luohuo.flex.ai.service.model.AiChatRoleService;
import com.luohuo.flex.ai.service.model.AiModelService;
import com.luohuo.flex.ai.utils.BeanUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.CHAT_CONVERSATION_MODEL_ERROR;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.CHAT_CONVERSATION_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;
import static com.luohuo.basic.utils.collection.CollectionUtils.convertList;


/**
 * AI 聊天对话 Service 实现类
 *
 * @author 乾乾
 */
@Service
@Validated
@Slf4j
public class AiChatConversationServiceImpl implements AiChatConversationService {

    @Resource
    private AiChatConversationMapper chatConversationMapper;

    @Resource
    private AiModelService modalService;
    @Resource
    private AiChatRoleService chatRoleService;
    @Resource
    private AiKnowledgeService knowledgeService;

    @Override
    public AiChatConversationDO createChatConversationMy(AiChatConversationCreateMyReqVO createReqVO, Long userId) {
        // 1.1 获得 AiChatRoleDO 聊天角色
        AiChatRoleDO role = createReqVO.getRoleId() != null ? chatRoleService.validateChatRole(createReqVO.getRoleId()) : null;
        // 1.2 获得 AiModelDO 聊天模型
        AiModelDO model = role != null && role.getModelId() != null ? modalService.validateModel(role.getModelId())
                : modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        Assert.notNull(model, "必须找到默认模型");
        validateChatModel(model);

        // 1.3 校验知识库
        if (Objects.nonNull(createReqVO.getKnowledgeId())) {
            knowledgeService.validateKnowledgeExists(createReqVO.getKnowledgeId());
        }

        // 2. 创建 AiChatConversationDO 聊天对话
        AiChatConversationDO conversation = new AiChatConversationDO().setUserId(userId).setPinned(false)
                .setModelId(model.getId()).setModel(model.getModel())
                .setTemperature(model.getTemperature()).setMaxTokens(model.getMaxTokens()).setMaxContexts(model.getMaxContexts())
                .setTokenUsage(0);
        if (role != null) {
            conversation.setTitle(role.getName()).setRoleId(role.getId()).setSystemMessage(role.getSystemMessage());
        } else {
            conversation.setTitle(AiChatConversationDO.TITLE_DEFAULT);
        }
        chatConversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public void updateChatConversationMy(AiChatConversationUpdateMyReqVO updateReqVO, Long userId) {
        // 1.1 校验对话是否存在
        AiChatConversationDO conversation = validateChatConversationExists(updateReqVO.getId());
        if (ObjUtil.notEqual(conversation.getUserId(), userId)) {
            throw exception(CHAT_CONVERSATION_NOT_EXISTS);
        }

        // 1.2 校验角色是否存在
        AiChatRoleDO role = null;
        if (updateReqVO.getRoleId() != null) {
            role = chatRoleService.validateChatRole(updateReqVO.getRoleId());
        }

        // 1.3 校验模型是否存在
        AiModelDO model = null;
        if (updateReqVO.getModelId() != null) {
            model = modalService.validateModel(updateReqVO.getModelId());
        }

        // 1.4 校验知识库是否存在
        if (updateReqVO.getKnowledgeId() != null) {
            knowledgeService.validateKnowledgeExists(updateReqVO.getKnowledgeId());
        }

        // 2. 更新对话信息
        AiChatConversationDO updateObj = BeanUtils.toBean(updateReqVO, AiChatConversationDO.class);
        // 禁止会话层直接修改 maxTokens / maxContexts，统一由模型驱动
        updateObj.setMaxTokens(null);
        updateObj.setMaxContexts(null);
        if (Boolean.TRUE.equals(updateReqVO.getPinned())) {
            updateObj.setPinnedTime(LocalDateTime.now());
        }
        if (role != null) {
            updateObj.setRoleId(role.getId());
            updateObj.setTitle(role.getName());
            updateObj.setSystemMessage(role.getSystemMessage());
        }
        if (model != null) {
            updateObj.setModelId(model.getId());
            updateObj.setModel(model.getModel());
            updateObj.setMaxTokens(model.getMaxTokens());
            updateObj.setMaxContexts(model.getMaxContexts());
        }
        chatConversationMapper.updateById(updateObj);
    }

    @Override
    public List<AiChatConversationDO> getChatConversationListByUserId(Long userId) {
        return chatConversationMapper.selectListByUserId(userId);
    }

    @Override
    public AiChatConversationDO getChatConversation(Long id) {
        return chatConversationMapper.selectById(id);
    }

    @Override
    public void deleteChatConversationMy(AiDelReqVO reqVO, Long userId) {
        // 1. 校验对话是否存在
		reqVO.getConversationIdList().forEach(id -> {
			AiChatConversationDO conversation = validateChatConversationExists(id);
			if (conversation == null || ObjUtil.notEqual(conversation.getUserId(), userId)) {
				throw exception(CHAT_CONVERSATION_NOT_EXISTS);
			}
		});

        // 2. 执行删除
        chatConversationMapper.deleteByIds(reqVO.getConversationIdList());
    }

    @Override
    public void deleteChatConversationByAdmin(Long id) {
        // 1. 校验对话是否存在
        AiChatConversationDO conversation = validateChatConversationExists(id);
        if (conversation == null) {
            throw exception(CHAT_CONVERSATION_NOT_EXISTS);
        }
        // 2. 执行删除
        chatConversationMapper.deleteById(id);
    }

    private void validateChatModel(AiModelDO model) {
        if (ObjectUtil.isAllNotEmpty(model.getTemperature(), model.getMaxTokens(), model.getMaxContexts())) {
            return;
        }
        Assert.equals(model.getType(), AiModelTypeEnum.CHAT.getType(), "模型类型不正确：" + model);
        throw exception(CHAT_CONVERSATION_MODEL_ERROR);
    }

    public AiChatConversationDO validateChatConversationExists(Long id) {
        AiChatConversationDO conversation = chatConversationMapper.selectById(id);
        if (conversation == null) {
            throw exception(CHAT_CONVERSATION_NOT_EXISTS);
        }
        return conversation;
    }

    @Override
    public void deleteChatConversationMyByUnpinned(Long userId) {
        List<AiChatConversationDO> list = chatConversationMapper.selectListByUserIdAndPinned(userId, false);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        chatConversationMapper.deleteBatchIds(convertList(list, AiChatConversationDO::getId));
    }

    @Override
    public PageResult<AiChatConversationDO> getChatConversationPage(AiChatConversationPageReqVO pageReqVO) {
        return chatConversationMapper.selectChatConversationPage(pageReqVO);
    }

}
