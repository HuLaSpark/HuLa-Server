package com.hula.common.domain.dto;

import lombok.*;

import java.util.concurrent.TimeUnit;

/**
 * 限流策略定义
 * @author nyh
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrequencyControlDTO {
    /**
     * 代表频控的Key 如果target为Key的话 这里要传值用于构建redis的Key target为Ip或者UID的话会从上下文取值 Key字段无需传值
     */
    private String key;
    /**
     * 频控时间范围，默认单位秒
     *
     * @return 时间范围
     */
    private Integer time;

    /**
     * 频控时间单位，默认秒
     *
     * @return 单位
     */
    private TimeUnit unit;

    /**
     * 单位时间内最大访问次数
     *
     * @return 次数
     */
    private Integer count;
}
