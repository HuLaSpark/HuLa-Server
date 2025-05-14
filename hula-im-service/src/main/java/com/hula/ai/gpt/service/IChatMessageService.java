package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.gpt.pojo.entity.ChatMessage;
import com.hula.ai.gpt.pojo.param.ChatMessageParam;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatMessageVO;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.core.chat.domain.vo.request.ChatMessagePageReq;

import java.util.List;

/**
 * 对话消息 服务类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IChatMessageService extends IService<ChatMessage> {

    /**
     * 查询对话消息分页列表
     *
     * @param param 查询条件
     * @return 对话消息集合
     */
	IPage<ChatMessageVO> pageChatMessage(ChatMessageParam param);

    /**
     * 查询对话消息列表
     *
     * @param param 查询条件
     * @return 对话消息集合
     */
	List<ChatMessageVO> listChatMessage(ChatParam param);

    CursorPageBaseResp<ChatMessage> getChatMessagePage(ChatMessagePageReq param);

    /**
     * 查询对话消息列表
     *
     * @param chatId 查询条件
     * @return 对话消息集合
     */
	List<ChatMessageDTO> listChatMessage(Long chatId);

    /**
     * 根据主键查询对话消息
     *
     * @param id 对话消息主键
     * @return 对话消息
     */
	ChatMessageVO getChatMessageById(Long id);

    /**
     * 根据消息id查询对话id
     *
     * @param messageId 对话消息主键
     * @return 对话消息
     */
	Long getChatIdByMessageId(String messageId);

    /**
     * 根据消息id查询对话id
     *
     * @param messageId 对话消息主键
     * @return 对话消息
     */
	ChatMessageDTO getChatByMessageId(String messageId);

    /**
     * 新增对话消息（流式输出使用）
     *
     * @param command 对话消息
     * @return 结果
     */
	String saveChatMessage(ChatCommand command);

    /**
     * 新增回复消息（流式输出使用）
     *
     * @param command 对话消息
     * @return 结果
     */
    int saveChatMessage(ChatMessageCommand command);

    /**
     * 新增对话消息（同步返回使用）
     *
     * @param command 对话消息
     * @return 结果
     */
	List<ChatMessageDTO> saveChatMessage(ChatCommand command, Long chatId, String messageId);

    /**
     * 修改对话状态
     *
     * @param messageId 消息id
     * @param status    状态
     * @return 结果
     */
    int updateMessageStatus(String messageId, Integer status);

    /**
     * 更新对话使用token数
     *
     * @param messageId  消息id
     * @param usedTokens 使用token数
     * @return
     */
    int updateMessageUsedTokens(String messageId, Long usedTokens);

    /**
     * 批量删除对话消息
     *
     * @param ids 需要删除的对话消息主键集合
     * @return 结果
     */
    int removeChatMessageByIds(List<Long> ids);

    /**
     * 删除对话消息信息
     *
     * @param messageId 对话消息主键
     * @return 结果
     */
    int removeChatMessageByMessageId(String messageId);

}
