package com.luohuo.flex.im.core.frequencyControl.dto;

import lombok.Data;

/**
 * web请求信息收集类
 * @author nyh
 */
@Data
public class RequestInfo {

    /**
     * 链路id
     */
    private Long uid;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 令牌
     */
    private String token;
}
