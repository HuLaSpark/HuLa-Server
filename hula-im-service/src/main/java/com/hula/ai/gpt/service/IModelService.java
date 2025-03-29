package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.ModelCommand;
import com.hula.ai.gpt.pojo.entity.Model;
import com.hula.ai.gpt.pojo.param.ModelParam;
import com.hula.ai.gpt.pojo.vo.ModelVO;

import java.util.List;

/**
 * 大模型信息 服务类
 *
 * @author: 云裂痕
 * @date: 2023-12-01
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IModelService extends IService<Model> {

    /**
     * 查询大模型信息分页列表
     *
     * @param param 查询条件
     * @return 大模型信息集合
     */
	IPage<ModelVO> pageModel(ModelParam param);

    /**
     * 查询大模型信息列表
     *
     * @param params 查询条件
     * @return 大模型信息集合
     */
	List<ModelVO> listModel(ModelParam params);

    /**
     * 根据主键查询大模型信息
     *
     * @param id 大模型信息主键
     * @return 大模型信息
     */
	ModelVO getModelById(Long id);

    /**
     * 新增大模型信息
     *
     * @param command 大模型信息
     * @return 结果
     */
    int saveModel(ModelCommand command);

    /**
     * 修改大模型信息
     *
     * @param command 大模型信息
     * @return 结果
     */
	int updateModel(ModelCommand command);

    /**
     * 批量删除大模型信息
     *
     * @param ids 需要删除的大模型信息主键集合
     * @return 结果
     */
	int removeModelByIds(List<Long> ids);
}
