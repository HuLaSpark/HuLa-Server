package com.hula.domain.dto;

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
}
