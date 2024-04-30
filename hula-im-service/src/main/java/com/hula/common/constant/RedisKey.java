package com.hula.common.constant;

/**
 * 管理项目通用key
 * @author nyh
 */
public class RedisKey {
    private static final String BASE_KEY = "hula:chat";
    /** 用户token的key */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";
    /** 获取key */
    public static String getKey(String key, Object... o) {
        return BASE_KEY + String.format(key, o);
    }
}
