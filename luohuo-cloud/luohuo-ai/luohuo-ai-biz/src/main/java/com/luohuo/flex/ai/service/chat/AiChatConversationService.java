package com.luohuo.flex.ai.service.chat;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiChatConversationCreateMyReqVO;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiDelReqVO;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiChatConversationPageReqVO;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiChatConversationUpdateMyReqVO;
import com.luohuo.flex.ai.dal.chat.AiChatConversationDO;

import java.util.List;

/**
 * AI 聊天对话 Service 接口
 *
 */
public interface AiChatConversationService {

    /**
     * 创建【我的】聊天对话
     *
     * @param createReqVO 创建信息
     * @param userId 用户编号
     * @return 编号
     */
	AiChatConversationDO createChatConversationMy(AiChatConversationCreateMyReqVO createReqVO, Long userId);

    /**
     * 更新【我的】聊天对话
     *
     * @param updateReqVO 更新信息
     * @param userId 用户编号
     */
    void updateChatConversationMy(AiChatConversationUpdateMyReqVO updateReqVO, Long userId);

    /**
     * 获得【我的】聊天对话列表
     *
     * @param userId 用户编号
     * @return 聊天对话列表
     */
    List<AiChatConversationDO> getChatConversationListByUserId(Long userId);

    /**
     * 获得聊天对话
     *
     * @param id 编号
     * @return 聊天对话
     */
    AiChatConversationDO getChatConversation(Long id);

    /**
     * 删除【我的】聊天对话
     *
     * @param reqVO 编号
     * @param userId 用户编号
     */
    void deleteChatConversationMy(AiDelReqVO reqVO, Long userId);

    /**
     * 【管理员】删除聊天对话
     *
     * @param id 编号
     */
    void deleteChatConversationByAdmin(Long id);

    /**
     * 校验聊天对话是否存在
     *
     * @param id 编号
     * @return 聊天对话
     */
    AiChatConversationDO validateChatConversationExists(Long id);

    /**
     * 删除【我的】 + 非置顶的聊天对话
     *
     * @param userId 用户编号
     */
    void deleteChatConversationMyByUnpinned(Long userId);

    /**
     * 获得聊天对话的分页列表
     *
     * @param pageReqVO 分页查询
     * @return 聊天对话的分页列表
     */
    PageResult<AiChatConversationDO> getChatConversationPage(AiChatConversationPageReqVO pageReqVO);

}
