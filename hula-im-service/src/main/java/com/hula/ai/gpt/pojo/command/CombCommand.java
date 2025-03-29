package com.hula.ai.gpt.pojo.command;

import com.hula.ai.common.api.CommonCommand;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  会员套餐对象 Command
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class CombCommand extends CommonCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 套餐名称
     */
    private String title;

    /**
     * 套餐类型
     */
    private Integer type;

    /**
     * 包含次数
     */
    private Long num;

    /**
     * 天数
     */
    private Long days;

    /**
     * 原价
     */
    private BigDecimal originPrice;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 状态
     */
    private Integer status;

}
