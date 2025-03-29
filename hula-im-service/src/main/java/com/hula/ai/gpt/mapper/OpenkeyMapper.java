package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Openkey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.OpenKeyParam;
import com.hula.ai.gpt.pojo.vo.OpenkeyVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * openai token Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
public interface OpenkeyMapper extends BaseMapper<Openkey> {

	/**
	 * 分页查询openai token列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<OpenkeyVO> pageOpenkey(IPage page, @Param("q") OpenKeyParam query);

	/**
	 * 查询openai token列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<OpenkeyVO> listOpenkey(@Param("q") OpenKeyParam query);

}
