package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.gpt.pojo.entity.Chat;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatVO;

import java.util.List;

/**
 * 聊天摘要 服务类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IChatService extends IService<Chat> {

    /**
     * 查询聊天摘要分页列表
     *
     * @param param 查询条件
     * @return 聊天摘要集合
     */
	IPage<ChatVO> pageChat(ChatParam param);

    /**
     * 查询聊天摘要列表
     *
     * @param param 查询条件
     * @return 聊天摘要集合
     */
	List<ChatVO> listChat(ChatParam param);

    /**
     * 根据主键查询聊天摘要
     *
     * @param id 聊天摘要主键
     * @return 聊天摘要
     */
	ChatVO getChatById(Long id);

    /**
     * 新增聊天摘要(同步)
     *
     * @param command 聊天摘要
     * @return 结果
     */
	Long saveChat(ChatCommand command);

    /**
     * 新增聊天摘要(SSE)
     *
     * @param command 聊天摘要
     * @return 结果
     */
	ChatVO saveSSEChat(ChatCommand command);

    /**
     * 批量删除聊天摘要
     *
     * @param ids 需要删除的聊天摘要主键集合
     * @return 结果
     */
	int removeChatByIds(List<Long> ids);

    /**
     * 删除聊天摘要信息
     *
     * @param chatNumber 聊天摘要主键
     * @return 结果
     */
    int removeChatByChatNumber(String chatNumber);
}
