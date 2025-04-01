package com.hula.ai.gpt.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *  订单对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

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
     * 用户名称
     */
    private String userName;

    /**
     * 购买套餐
     */
    private Long combId;

    /**
     * 套餐名称
     */
    private String combName;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 支付渠道 1 微信小程序 2、微信公众号 3、微信h5 4、微信扫码
     */
    private Integer chanel;

    /**
     * 订单状态 1 待支付 2 支付成功 3 支付超时 4 已退款
     */
    private Integer status;

}
