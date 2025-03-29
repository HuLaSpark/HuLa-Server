package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.OrderCommand;
import com.hula.ai.gpt.pojo.entity.Order;
import com.hula.ai.gpt.pojo.param.OrderParam;
import com.hula.ai.gpt.pojo.vo.OrderVO;

import java.util.List;

/**
 * 订单 服务类
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
public interface IOrderService extends IService<Order> {

    /**
     * 查询订单分页列表
     *
     * @param param 查询条件
     * @return 订单集合
     */
    IPage<OrderVO> pageOrder(OrderParam param);

    /**
     * 查询订单列表
     *
     * @param param 查询条件
     * @return 订单集合
     */
    List<OrderVO> listOrder(OrderParam param);

    /**
     * 根据主键查询订单
     *
     * @param id 订单主键
     * @return 订单
     */
     OrderVO getOrderById(Long id);

    /**
     * 新增订单
     *
     * @param command 订单
     * @return 结果
     */
    int saveOrder(OrderCommand command);

    /**
     * 修改订单
     *
     * @param command 订单
     * @return 结果
     */
	int updateOrder(OrderCommand command);

    /**
     * 批量删除订单
     *
     * @param ids 需要删除的订单主键集合
     * @return 结果
     */
	int removeOrderByIds(List<Long> ids);

}
