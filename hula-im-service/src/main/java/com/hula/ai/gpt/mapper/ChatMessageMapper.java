package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.ChatMessageParam;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatMessageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 对话消息 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
	/**
	 * 分页查询对话消息列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<ChatMessageVO> pageChatMessage(IPage page, @Param("q") ChatMessageParam query);

	/**
	 * 查询对话消息列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<ChatMessageVO> listChatMessage(@Param("q") ChatParam query);


    /**
     * 更新消息状态
     *
     * @param messageId
     * @param status
     */
    @Update("update ai_gpt_chat_message t set t.status = #{status} where t.message_id = #{messageId}")
    void updateMessageStatus(@Param("messageId") String messageId, @Param("status") Integer status);

    /**
     * 更新消息使用token
     *
     * @param messageId  消息id
     * @param usedTokens 使用token
     */
    @Update("update ai_gpt_chat_message t set t.used_tokens = #{usedTokens} where t.message_id = #{messageId}")
    void updateMessageUsedTokens(@Param("messageId") String messageId, @Param("usedTokens") Long usedTokens);

}
