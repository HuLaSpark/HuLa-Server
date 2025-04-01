package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.AssistantCommand;
import com.hula.ai.gpt.pojo.entity.Assistant;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.param.AssustantParams;
import com.hula.ai.gpt.pojo.vo.AppAssistantVO;
import com.hula.ai.gpt.pojo.vo.AssistantVO;
import com.hula.domain.vo.res.ApiResult;

import java.util.List;

/**
 * AI助理功能 服务类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IAssistantService extends IService<Assistant> {

    /**
     * 查询AI助理功能分页列表
     *
     * @param param 查询条件
     * @return AI助理功能集合
     */
	IPage<AssistantVO> pageAssistant(AssustantParams param);

    /**
     * 查询AI助理功能列表
     *
     * @param param 查询条件
     * @return AI助理功能集合
     */
	List<AssistantVO> listAssistant(AssustantParams param);

    /**
     * 查询AI助理功能列表
     *
     * @param param 查询条件
     * @return AI助理功能集合
     */
	List<AppAssistantVO> listAssistantByApp(AgreementParam param);

    /**
     * 根据主键查询AI助理功能
     *
     * @param id AI助理功能主键
     * @return AI助理功能
     */
	ApiResult<AssistantVO> getAssistantById(Long id);

    /**
     * 新增AI助理功能
     *
     * @param command AI助理功能
     * @return 结果
     */
	ApiResult saveAssistant(AssistantCommand command);

    /**
     * 修改AI助理功能
     *
     * @param command AI助理功能
     * @return 结果
     */
	ApiResult updateAssistant(AssistantCommand command);

    /**
     * 批量删除AI助理功能
     *
     * @param ids 需要删除的AI助理功能主键集合
     * @return 结果
     */
	ApiResult removeAssistantByIds(List<Long> ids);

    /**
     * 删除AI助理功能信息
     *
     * @param id AI助理功能主键
     * @return 结果
     */
	ApiResult removeAssistantById(Long id);

}
