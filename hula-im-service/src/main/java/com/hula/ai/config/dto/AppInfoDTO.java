package com.hula.ai.config.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * APP信息
 *
 * @author: 云裂痕
 * @date: 2025/03/07
 * 得其道 乾乾
 */
@Data
public class AppInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否无限制访问GPT
     */
    private Integer isGPTLimit;

    /**
     * 是否开启兑换码
     */
    private Integer isRedemption;

    /**
     * 是否开启短信
     */
    private Integer isSms;

    /**
     * 是否开启分享
     */
    private Integer isShare;

    /**
     * 分享赠送次数
     */
    private Integer shareNum;

    /**
     * 免费体验次数
     */
    private Integer freeNum;

    /**
     * H5地址
     */
    private String h5Url;

    /**
     * 首页公告
     */
    private String homeNotice;

}
