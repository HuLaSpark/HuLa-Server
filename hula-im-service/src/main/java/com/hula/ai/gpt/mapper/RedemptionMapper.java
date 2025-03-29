package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Redemption;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.RedemptionParam;
import com.hula.ai.gpt.pojo.vo.RedemptionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 兑换码 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
public interface RedemptionMapper extends BaseMapper<Redemption> {

	/**
	 * 分页查询兑换码列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<RedemptionVO> pageRedemption(IPage page, @Param("q") RedemptionParam query);

	/**
	 * 查询兑换码列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<RedemptionVO> listRedemption(@Param("q") RedemptionParam query);
}
