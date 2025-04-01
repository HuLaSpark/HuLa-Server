package com.hula.ai.common.constant;

/**
 * redis常量类
 *
 * @author: 云裂痕
 * @date: 2020/3/4
 * @version: 1.0.0
 * 得其道 乾乾
 */
public interface RedisConstant {

    /**
     * 默认redis过期时长，单位：秒 一年
     */
    Long YEAT_EXPIRE = 12 * 30 * 24 * 3600L;

    /**
     * 默认redis过期时长，单位：秒 一个月
     */
    Long DEFAULT_EXPIRE = 30 * 24 * 3600L;

    /**
     * 失效时间 单位：（秒）一周
     */
    Long WEEK_EXPIRE = 7 * 24 * 3600L;

    /**
     * 失效时间 单位：（秒）一天
     */
    Long DAY_EXPIRE = 24 * 3600L;

    /**
     * 失效时间 单位：（秒）一小时
     */
    Long HOUR_SECONDS = 3600L;

    /**
     * 失效时间 单位：（秒）五分钟
     */
    Long FIVE_MINUTES = 5 * 60L;

    /**
     * 失效时间 单位：（秒）一分钟
     */
    Long ONE_MINUTES = 60L;

    /**
     * 限流 redis key
     */
    String RATE_LIMIT_KEY = "rate_limit:";

}
