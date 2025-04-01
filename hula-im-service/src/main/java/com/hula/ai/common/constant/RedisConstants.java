package com.hula.ai.common.constant;

/**
 * 缓存的key 常量
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class RedisConstants implements RedisConstant {

    /**
     * 有效期7天
     */
    public static final long EXPIRE_TIME = 7 * 24 * 3600 * 1000;

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";
}
