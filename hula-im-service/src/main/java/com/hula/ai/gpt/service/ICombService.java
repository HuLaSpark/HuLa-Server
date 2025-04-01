package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.CombCommand;
import com.hula.ai.gpt.pojo.entity.Comb;
import com.hula.ai.gpt.pojo.param.CombParam;
import com.hula.ai.gpt.pojo.vo.CombVO;

import java.util.List;

/**
 * 会员套餐 服务类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface ICombService extends IService<Comb> {

    /**
     * 查询会员套餐分页列表
     *
     * @param param 查询条件
     * @return 会员套餐集合
     */
	IPage<CombVO> pageComb(CombParam param);

    /**
     * 查询会员套餐列表
     *
     * @param param 查询条件
     * @return 会员套餐集合
     */
	List<CombVO> listComb(CombParam param);

    /**
     * 根据主键查询会员套餐
     *
     * @param id 会员套餐主键
     * @return 会员套餐
     */
	CombVO getCombById(Long id);

    /**
     * 新增会员套餐
     *
     * @param command 会员套餐
     * @return 结果
     */
    int saveComb(CombCommand command);

    /**
     * 修改会员套餐
     *
     * @param command 会员套餐
     * @return 结果
     */
	int updateComb(CombCommand command);

    /**
     * 批量删除会员套餐
     *
     * @param ids 需要删除的会员套餐主键集合
     * @return 结果
     */
	int removeCombByIds(List<Long> ids);
}
