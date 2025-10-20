package com.luohuo.flex.service;

import com.luohuo.flex.dao.ModelDao;
import com.luohuo.flex.model.Model;
import com.luohuo.flex.res.ModelVO;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型服务实现类
 */
@Service
public class ModelService {

	@Resource
	private ModelDao modelDao;

	/**
	 * 查询所有启用的模型列表
	 */
	@Cacheable(cacheNames = "luohuo:model", key = "'list'")
	public List<ModelVO> getAllModels() {
		return modelDao.selectAllEnabledModels();
	}

	/**
	 * 根据模型键名查询模型
	 */
	public Model selectByModelKey(String modelKey) {
		return modelDao.lambdaQuery()
				.eq(Model::getModelKey, modelKey)
				.one();
	}

	/**
	 * 更新模型状态
	 */
	public boolean updateModelStatus(Long id, Boolean status) {
		Model model = new Model();
		model.setId(id);
		model.setStatus(status);
		return modelDao.updateById(model);
	}

	/**
	 * 新增模型
	 */
	public boolean save(Model model) {
		if (model.getStatus() == null) {
			model.setStatus(true);
		}
		return modelDao.save(model);
	}
}