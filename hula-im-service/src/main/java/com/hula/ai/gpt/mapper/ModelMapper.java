package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Model;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.ModelParam;
import com.hula.ai.gpt.pojo.vo.ModelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 大模型信息 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
public interface ModelMapper extends BaseMapper<Model> {

	/**
	 * 查询大模型信息
	 *
	 * @param model 查询条件
	 * @return
	 */
	ModelVO getModel(@Param("model") String model);

	/**
	 * 分页查询大模型信息列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<ModelVO> pageModel(IPage page, @Param("q") ModelParam query);

	/**
	 * 查询大模型信息列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<ModelVO> listModel(@Param("q") ModelParam query);
}
