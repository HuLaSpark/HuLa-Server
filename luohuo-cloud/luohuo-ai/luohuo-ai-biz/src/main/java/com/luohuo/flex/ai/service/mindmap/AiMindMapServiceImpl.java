package com.luohuo.flex.ai.service.mindmap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapGenerateReqVO;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapPageReqVO;
import com.luohuo.flex.ai.dal.mindmap.AiMindMapDO;
import com.luohuo.flex.ai.dal.model.AiChatRoleDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiChatRoleEnum;
import com.luohuo.flex.ai.enums.AiModelTypeEnum;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.ErrorCodeConstants;
import com.luohuo.flex.ai.mapper.mindmap.AiMindMapMapper;
import com.luohuo.flex.ai.service.model.AiChatRoleService;
import com.luohuo.flex.ai.service.model.AiModelService;
import com.luohuo.flex.ai.utils.AiUtils;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static com.luohuo.flex.ai.common.pojo.CommonResult.error;
import static com.luohuo.basic.base.R.success;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.*;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;


/**
 * AI 思维导图 Service 实现类
 *
 */
@Service
@Slf4j
public class AiMindMapServiceImpl implements AiMindMapService {

    @Resource
    private AiModelService modalService;
    @Resource
    private AiChatRoleService chatRoleService;

    @Resource
    private AiMindMapMapper mindMapMapper;

    @Override
    public Flux<R<String>> generateMindMap(AiMindMapGenerateReqVO generateReqVO, Long userId) {
        // 1. 获取导图模型。尝试获取思维导图助手角色，如果没有则使用默认模型
        AiChatRoleDO role = CollUtil.getFirst(
                chatRoleService.getChatRoleListByName(AiChatRoleEnum.AI_MIND_MAP_ROLE.getName()));
        // 1.1 获取导图执行模型
        AiModelDO model = getModel(role);
        // 1.2 获取角色设定消息
        String systemMessage = role != null && StrUtil.isNotBlank(role.getSystemMessage())
                ? role.getSystemMessage() : AiChatRoleEnum.AI_MIND_MAP_ROLE.getSystemMessage();
        // 1.3 校验平台
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatModel chatModel = modalService.getChatModel(model.getId());

        // 2. 插入思维导图信息
        AiMindMapDO mindMapDO = BeanUtils.toBean(generateReqVO, AiMindMapDO.class, mindMap -> mindMap.setUserId(userId)
                .setPlatform(platform.getPlatform()).setModelId(model.getId()).setModel(model.getModel()));
        mindMapMapper.insert(mindMapDO);

        // 3.1 构建 Prompt，并进行调用
        Prompt prompt = buildPrompt(generateReqVO, model, systemMessage);
        Flux<ChatResponse> streamResponse = chatModel.stream(prompt);

        // 3.2 流式返回
        StringBuffer contentBuffer = new StringBuffer();
        return streamResponse.map(chunk -> {
            String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : null;
            newContent = StrUtil.nullToDefault(newContent, ""); // 避免 null 的 情况
            contentBuffer.append(newContent);
            // 响应结果
            return success(newContent);
        }).doOnComplete(() -> {
        }).doOnError(throwable -> {
            log.error("[generateWriteContent][generateReqVO({}) 发生异常]", generateReqVO, throwable);
        }).onErrorResume(error -> Flux.just(error(ErrorCodeConstants.WRITE_STREAM_ERROR)));

    }

    private Prompt buildPrompt(AiMindMapGenerateReqVO generateReqVO, AiModelDO model, String systemMessage) {
        // 1. 构建 message 列表
        List<Message> chatMessages = buildMessages(generateReqVO, systemMessage);
        // 2. 构建 options 对象
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(), model.getTemperature(), model.getMaxTokens());
        return new Prompt(chatMessages, options);
    }

    private static List<Message> buildMessages(AiMindMapGenerateReqVO generateReqVO, String systemMessage) {
        List<Message> chatMessages = new ArrayList<>();
        // 1. 角色设定
        if (StrUtil.isNotBlank(systemMessage)) {
            chatMessages.add(new SystemMessage(systemMessage));
        }
        // 2. 用户输入
        chatMessages.add(new UserMessage(generateReqVO.getPrompt()));
        return chatMessages;
    }

    private AiModelDO getModel(AiChatRoleDO role) {
        AiModelDO model = null;
        if (role != null && role.getModelId() != null) {
            model = modalService.getModel(role.getModelId());
        }
        if (model == null) {
            model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        }
        // 校验模型存在、且合法
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(model.getType(), AiModelTypeEnum.CHAT.getType())) {
            throw exception(MODEL_USE_TYPE_ERROR);
        }
        return model;
    }

    @Override
    public void deleteMindMap(Long id) {
        // 校验存在
        validateMindMapExists(id);
        // 删除
        mindMapMapper.deleteById(id);
    }

    private void validateMindMapExists(Long id) {
        if (mindMapMapper.selectById(id) == null) {
            throw exception(MIND_MAP_NOT_EXISTS);
        }
    }

    @Override
    public PageResult<AiMindMapDO> getMindMapPage(AiMindMapPageReqVO pageReqVO) {
        return mindMapMapper.selectPage(pageReqVO);
    }

}
