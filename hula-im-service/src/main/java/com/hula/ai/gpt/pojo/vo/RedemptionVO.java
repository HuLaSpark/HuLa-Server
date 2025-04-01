package com.hula.ai.gpt.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  兑换码对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道 乾乾
 */
@Data
public class RedemptionVO implements Serializable {

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
     * 兑换码
     */
    private String code;

    /**
     * 可兑次数
     */
    private Long num;

    /**
     * 兑换人
     */
    private Long userId;

    /**
     * 兑换人
     */
    private String userName;

    /**
     * 兑换时间
     */
    private Integer recieveTime;

    /**
     * 状态 0 未兑换 1 已兑换
     */
    private Integer status;

}
