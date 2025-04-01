package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.AssistantType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.param.AssustantTypeParams;
import com.hula.ai.gpt.pojo.vo.AssistantTypeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 助手分类 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2023-11-22
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface AssistantTypeMapper extends BaseMapper<AssistantType> {

	/**
	 * 分页查询助手分类列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<AssistantTypeVO> pageAssistantType(IPage page, @Param("q") AssustantTypeParams query);

	/**
	 * 查询助手分类列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<AssistantTypeVO> listAssistantType(@Param("q") AgreementParam query);

}
