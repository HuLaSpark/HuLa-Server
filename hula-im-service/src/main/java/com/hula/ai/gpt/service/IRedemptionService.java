package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.RedemptionCommand;
import com.hula.ai.gpt.pojo.entity.Redemption;
import com.hula.ai.gpt.pojo.param.RedemptionParam;
import com.hula.ai.gpt.pojo.vo.RedemptionVO;

import java.util.List;

/**
 * 兑换码 服务类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IRedemptionService extends IService<Redemption> {

    /**
     * 查询兑换码分页列表
     *
     * @param param 查询条件
     * @return 兑换码集合
     */
	IPage<RedemptionVO> pageRedemption(RedemptionParam param);

    /**
     * 查询兑换码列表
     *
     * @param param 查询条件
     * @return 兑换码集合
     */
    List<RedemptionVO> listRedemption(RedemptionParam param);

    /**
     * 根据主键查询兑换码
     *
     * @param id 兑换码主键
     * @return 兑换码
     */
	RedemptionVO getRedemptionById(Long id);

    /**
     * 新增兑换码
     *
     * @param command 兑换码
     * @return 结果
     */
    int saveRedemption(RedemptionCommand command);

    /**
     * 修改兑换码
     *
     * @param command 兑换码
     * @return 结果
     */
    int updateRedemption(RedemptionCommand command);

    /**
     * 批量删除兑换码
     *
     * @param ids 需要删除的兑换码主键集合
     * @return 结果
     */
    int removeRedemptionByIds(List<Long> ids);
}
