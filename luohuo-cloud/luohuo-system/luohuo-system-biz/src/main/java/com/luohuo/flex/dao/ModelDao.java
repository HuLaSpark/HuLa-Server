package com.luohuo.flex.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.model.Model;
import com.luohuo.flex.res.ModelVO;
import com.luohuo.flex.mapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统模型 数据访问层实现类
 */
@Service
public class ModelDao extends ServiceImpl<ModelMapper, Model> implements IService<Model> {
	public List<ModelVO> selectAllEnabledModels() {
		return baseMapper.selectAllEnabledModels();
	}

    public Model selectByModelKey(String modelKey) {
        return lambdaQuery()
                .eq(Model::getModelKey, modelKey)
                .one();
    }

    public boolean updateModelStatus(Long id, Boolean status) {
        Model entity = new Model();
        entity.setId(id);
        entity.setStatus(status);
        return updateById(entity);
    }

    public boolean existsByModelKey(String modelKey) {
        return lambdaQuery()
                .eq(Model::getModelKey, modelKey)
                .exists();
    }
}