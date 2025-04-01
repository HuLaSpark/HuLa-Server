package com.hula.ai.config.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信信息
 *
 * @author: 云裂痕
 * @date: 2023/5/6
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class WxInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否使用小程序
     */
    private Integer isWxapp;

    /**
     * 小程序id
     */
    private String wxAppId;

    /**
     * 小程序密钥
     */
    private String wxAppSecret;

    /**
     * 是否使用公众号
     */
    private Integer isMp;

    /**
     * 公众号id
     */
    private String mpAppId;

    /**
     * 公众号密钥
     */
    private String mpSecret;

    /**
     * 是否开启微信支付
     */
    private Integer isWxPay;

    /**
     * 是否开启服务商支付
     */
    private Integer isWxSpPay;

    /**
     * 服务商AppId
     */
    private String spAppId;

    /**
     * 服务商商户号
     */
    private String spMchId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * V3密钥
     */
    private String apiV3Key;

    /**
     * 应用私钥
     */
    private String privateKey;

    /**
     * 证书序列号
     */
    private String mchSerialNo;

    /**
     * 支付证书信息
     */
    private String certPath;

}
