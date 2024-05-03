package com.hula.common.constant;

/**
 * 管理项目通用key
 * @author nyh
 */
public class RedisKey {
    private static final String BASE_KEY = "hula:";
    /**
     * 用户token的key
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";
    /**
     * 保存Open id
     */
    public static final String OPEN_ID_STRING = "openid:%s";
    /** 获取key */
    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }
}
