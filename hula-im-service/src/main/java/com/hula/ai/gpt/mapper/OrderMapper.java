package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.OrderParam;
import com.hula.ai.gpt.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
public interface OrderMapper extends BaseMapper<Order> {

	/**
	 * 分页查询订单列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<OrderVO> pageOrder(IPage page, @Param("q") OrderParam query);

	/**
	 * 查询订单列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<OrderVO> listOrder(@Param("q") OrderParam query);
}
