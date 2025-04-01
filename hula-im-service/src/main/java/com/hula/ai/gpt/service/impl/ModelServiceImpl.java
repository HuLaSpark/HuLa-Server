package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.ModelMapper;
import com.hula.ai.gpt.pojo.command.ModelCommand;
import com.hula.ai.gpt.pojo.entity.Model;
import com.hula.ai.gpt.pojo.param.ModelParam;
import com.hula.ai.gpt.pojo.vo.ModelVO;
import com.hula.ai.gpt.service.IModelService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  大模型信息 服务实现类
 *
 * @author: 云裂痕
 * @date: 2023-12-01
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements IModelService {
    @Autowired
    private ModelMapper modelMapper;

    /**
     * 根据id获取大模型信息信息
     *
     * @param id 大模型信息id
     * @return
     */
    private Model getModel(Long id) {
        Model model = modelMapper.selectById(id);
        if (ObjectUtil.isNull(model)) {
            throw new BizException("大模型信息信息不存在，无法操作");
        }
        return model;
    }

    @Override
    public IPage<ModelVO> pageModel(ModelParam param) {
		return modelMapper.pageModel(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<ModelVO> listModel(ModelParam param) {
		return modelMapper.listModel(param);
    }

    @Override
    public ModelVO getModelById(Long id) {
        return DozerUtil.convertor(getModel(id), ModelVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveModel(ModelCommand command) {
        Model model = DozerUtil.convertor(command, Model.class);
        model.setCreatedBy(command.getOperater());
		return modelMapper.insert(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateModel(ModelCommand command) {
        Model model = getModel(command.getId());
        DozerUtil.convertor(command, model);
        model.setUpdatedBy(command.getOperater());
        model.setUpdatedTime(LocalDateTime.now());
		return modelMapper.updateById(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeModelByIds(List<Long> ids) {
		return modelMapper.deleteBatchIds(ids);
    }

}
