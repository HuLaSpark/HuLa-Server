package com.hula.ai.gpt.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  openai token对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class OpenkeyVO implements Serializable {

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
     * appid
     */
    private String appId;

    /**
     * appkey
     */
    private String appKey;

    /**
     * app密钥
     */
    private String appSecret;


    /**
     * 总额度
     */
    private Long totalTokens;

    /**
     * 已用额度
     */
    private Long usedTokens;

    /**
     * 剩余额度
     */
    private Long surplusTokens;

    /**
     * 是否可用 0 禁用 1 启用
     */
    private Integer status;

    /**
     * 模型
     */
    private String model;

    /**
     * 备注
     */
    private String remark;

}
