package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Assistant;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.param.AssustantParams;
import com.hula.ai.gpt.pojo.vo.AssistantVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI助理功能 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface AssistantMapper extends BaseMapper<Assistant> {

	/**
	 * 分页查询AI助理功能列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<AssistantVO> pageAssistant(IPage page, @Param("q") AssustantParams query);

	/**
	 * 查询AI助理功能列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<AssistantVO> listAssistant(@Param("q") AssustantParams query);

	/**
	 * 查询AI助理功能列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<AssistantVO> listAssistantRandom(@Param("q") AgreementParam query);

}
