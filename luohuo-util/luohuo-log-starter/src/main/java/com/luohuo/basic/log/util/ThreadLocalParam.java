package com.luohuo.basic.log.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 线程变量封装的参数
 *
 * @author 乾乾
 * @date 2020/11/1 2:10 下午
 */
@Data
public class ThreadLocalParam implements Serializable {
    private Boolean boot;
    private String tenant;
    private Long userid;
    private String name;
    private String account;
}
