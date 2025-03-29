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
 *  会员套餐对象 gpt_comb
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
@TableName("ai_gpt_comb")
public class Comb implements Serializable {

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
     * 套餐名称
     */
    private String title;

    /**
     * 套餐类型 1 次数 2 天数
     */
    private Integer type;

    /**
     * 包含次数/天数
     */
    private Long num;

    /**
     * 原价
     */
    private BigDecimal originPrice;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 状态 0 禁用 1 启用
     */
    private Integer status;
}
