package com.luohuo.flex.im.core.frequencyControl.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 限流策略定义
 * @author nyh
 */
@Data
public class FrequencyControlDTO implements Serializable {
    /**
     * 代表频控的Key 如果target为Key的话 这里要传值用于构建redis的Key target为Ip或者UID的话会从上下文取值 Key字段无需传值
     */
    private String key;

    /**
     * 频控时间单位，默认秒
     *
     * 单位
     */
    private TimeUnit unit;

    /**
     * 单位时间内最大访问次数
     * 次数
     */
    private Integer count;

    /**
     * 单位时间
     *
     * 单位时间
     */
    private Integer time;


}
