package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.OrderMapper;
import com.hula.ai.gpt.pojo.command.OrderCommand;
import com.hula.ai.gpt.pojo.entity.Order;
import com.hula.ai.gpt.pojo.param.OrderParam;
import com.hula.ai.gpt.pojo.vo.OrderVO;
import com.hula.ai.gpt.service.IOrderService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  订单 服务实现类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 根据id获取订单信息
     *
     * @param id 订单id
     * @return
     */
    private Order getOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (ObjectUtil.isNull(order)) {
            throw new BizException("订单信息不存在，无法操作");
        }
        return order;
    }

    @Override
    public IPage<OrderVO> pageOrder(OrderParam param) {
		return orderMapper.pageOrder(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<OrderVO> listOrder(OrderParam param) {
		return orderMapper.listOrder(param);
    }

    @Override
    public OrderVO getOrderById(Long id) {
        return DozerUtil.convertor(getOrder(id), OrderVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveOrder(OrderCommand command) {
        Order order = DozerUtil.convertor(command, Order.class);
        order.setCreatedBy(command.getOperater());
		return orderMapper.insert(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOrder(OrderCommand command) {
        Order order = getOrder(command.getId());
        DozerUtil.convertor(command, order);
        order.setUpdatedBy(command.getOperater());
        order.setUpdatedTime(LocalDateTime.now());
		return orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeOrderByIds(List<Long> ids) {
		return orderMapper.deleteBatchIds(ids);
    }
}
