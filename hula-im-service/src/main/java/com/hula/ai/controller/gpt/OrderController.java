package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.command.OrderCommand;
import com.hula.ai.gpt.pojo.param.OrderParam;
import com.hula.ai.gpt.pojo.vo.OrderVO;
import com.hula.ai.gpt.service.IOrderService;
import com.hula.domain.vo.res.ApiResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  订单接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/gpt/order")
public class OrderController {
    @Resource
    private IOrderService orderService;

    /**
     * 查询订单分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<OrderVO>> pageOrder(OrderParam param) {
        return ApiResult.success(orderService.pageOrder(param));
    }

    /**
     * 查询订单列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<OrderVO>> listOrder(@RequestBody OrderParam param) {
        return ApiResult.success(orderService.listOrder(param));
    }

    /**
     * 获取订单详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<OrderVO> getOrderById(@PathVariable("id") Long id) {
        return ApiResult.success(orderService.getOrderById(id));
    }

    /**
     * 新增订单
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult saveOrder(@Validated @RequestBody OrderCommand command) {
        return ApiResult.returnResult("新增", orderService.saveOrder(command));
    }

    /**
     * 修改订单
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PutMapping
    public ApiResult updateOrder(@Validated @RequestBody OrderCommand command) {
        return ApiResult.returnResult("修改", orderService.updateOrder(command));
    }

    /**
     * 批量删除订单
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeOrderByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", orderService.removeOrderByIds(ids));
    }

}
