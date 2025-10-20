package com.luohuo.flex.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.model.Model;
import com.luohuo.flex.res.ModelVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统模型 Mapper 接口
 */
@Repository
public interface ModelMapper extends BaseMapper<Model> {
	/**
	 * 查询所有启用的模型列表
	 */
	List<ModelVO> selectAllEnabledModels();
}