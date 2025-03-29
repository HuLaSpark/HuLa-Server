package com.hula.ai.gpt.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单对象 gpt_order
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("ai_gpt_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	protected Long id;

	@Schema(description = "创建时间")
	protected LocalDateTime createdTime;

	@Schema(description = "创建人ID")
	protected Long createdBy;

	@Schema(description = "最后修改时间")
	protected LocalDateTime updatedTime;

	@Schema(description = "最后修改人ID")
	protected Long updatedBy;


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
    private Long uid;

    /**
     * 购买套餐
     */
    private Long combId;

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
