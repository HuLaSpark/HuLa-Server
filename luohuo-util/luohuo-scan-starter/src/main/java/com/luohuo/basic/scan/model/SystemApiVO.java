package com.luohuo.basic.scan.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author 乾乾
 * @date 2021/4/7 11:49 上午
 */
@Data
@Builder
@ToString
public class SystemApiVO {

    /**
     * 接口名称
     */
    private String name;
    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求路径
     */
    private String uri;

    /**
     * 服务ID
     */
    private String springApplicationName;

    /**
     * 类名
     */
    private String controller;
}
