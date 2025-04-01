package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.AssistantTypeCommand;
import com.hula.ai.gpt.pojo.entity.AssistantType;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.param.AssustantTypeParams;
import com.hula.ai.gpt.pojo.vo.AssistantTypeVO;

import java.util.List;

/**
 * 助手分类 服务类
 *
 * @author: 云裂痕
 * @date: 2023-11-22
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IAssistantTypeService extends IService<AssistantType> {

    /**
     * 查询助手分类分页列表
     *
     * @param param 查询条件
     * @return 助手分类集合
     */
    IPage<AssistantTypeVO> pageAssistantType(AssustantTypeParams param);

    /**
     * 查询助手分类列表
     *
     * @param param 查询条件
     * @return 助手分类集合
     */
	List<AssistantTypeVO> listAssistantType(AgreementParam param);

    /**
     * 根据主键查询助手分类
     *
     * @param id 助手分类主键
     * @return 助手分类
     */
	AssistantTypeVO getAssistantTypeById(Long id);

    /**
     * 新增助手分类
     *
     * @param command 助手分类
     * @return 结果
     */
    int saveAssistantType(AssistantTypeCommand command);

    /**
     * 修改助手分类
     *
     * @param command 助手分类
     * @return 结果
     */
    int updateAssistantType(AssistantTypeCommand command);

    /**
     * 批量删除助手分类
     *
     * @param ids 需要删除的助手分类主键集合
     * @return 结果
     */
    int removeAssistantTypeByIds(List<Long> ids);
}
