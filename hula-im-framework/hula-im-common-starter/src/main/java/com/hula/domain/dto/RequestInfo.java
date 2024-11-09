package com.hula.domain.dto;

import lombok.Data;

/**
 * web请求信息收集类
 * @author nyh
 */
@Data
public class RequestInfo {
    private Long uid;
    private String ip;
}
