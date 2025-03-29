package com.hula.ai.gpt.pojo.command;

import com.hula.ai.common.api.CommonCommand;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *  订单对象 Command
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class OrderCommand extends CommonCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 支付成功时间
     */
    private LocalDateTime successTime;

    /**
     * 订单号
     */
    private String tradeNo;

    /**
     * 渠道交易ID
     */
    private String transactionId;

    /**
     * 下单用户
     */
    private Long userId;

    /**
     * 购买套餐
     */
    private Long combId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 支付渠道
     */
    private Integer chanel;

    /**
     * 订单状态
     */
    private Integer status;

}
