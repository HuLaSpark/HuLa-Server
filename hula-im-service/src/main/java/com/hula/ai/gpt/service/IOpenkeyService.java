package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.OpenkeyCommand;
import com.hula.ai.gpt.pojo.entity.Openkey;
import com.hula.ai.gpt.pojo.param.OpenKeyParam;
import com.hula.ai.gpt.pojo.vo.OpenkeyVO;

import java.util.List;

/**
 * openai token 服务类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IOpenkeyService extends IService<Openkey> {

	/**
	 * 查询模型的密钥
	 * @param model 模型
	 * @return
	 */
	List<OpenkeyVO> listOpenkeyByModel(String model);

    /**
     * 查询openai token分页列表
     *
     * @param param 查询条件
     * @return openai token集合
     */
    IPage<OpenkeyVO> pageOpenkey(OpenKeyParam param);

    /**
     * 查询openai token列表
     *
     * @param param 查询条件
     * @return openai token集合
     */
    List<OpenkeyVO> listOpenkey(OpenKeyParam param);

    /**
     * 根据主键查询openai token
     *
     * @param id openai token主键
     * @return openai token
     */
	OpenkeyVO getOpenkeyById(Long id);

    /**
     * 新增openai token
     *
     * @param command openai token
     * @return 结果
     */
    int saveOpenkey(OpenkeyCommand command);

    /**
     * 修改openai token
     *
     * @param command openai token
     * @return 结果
     */
    int updateOpenkey(OpenkeyCommand command);

    /**
     * 批量删除openai token
     *
     * @param ids 需要删除的openai token主键集合
     * @return 结果
     */
    int removeOpenkeyByIds(List<Long> ids);
}
