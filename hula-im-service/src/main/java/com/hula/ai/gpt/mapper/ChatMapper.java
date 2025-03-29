package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天摘要 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface ChatMapper extends BaseMapper<Chat> {

	/**
	 * 分页查询聊天摘要列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<ChatVO> pageChat(IPage page, @Param("q") ChatParam query);

	/**
	 * 查询聊天摘要列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<ChatVO> listChat(@Param("q") ChatParam query);
}
