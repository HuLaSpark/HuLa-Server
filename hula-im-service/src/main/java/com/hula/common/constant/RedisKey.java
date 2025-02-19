package com.hula.common.constant;

/**
 * 管理项目通用key
 * @author nyh
 */
public class RedisKey {

    /**
     * 应用缓存
     */
    private static final String BASE_KEY = "hula:";

    /**
     * 在线用户列表
     */
    public static final String ONLINE_UID_ZET = "online";

    /**
     * 离线用户列表
     */
    public static final String OFFLINE_UID_ZET = "offline";

    /**
     * 热门房间列表
     */
    public static final String HOT_ROOM_ZET = "hotRoom";

    /**
     * 用户信息
     */
    public static final String USER_INFO_FORMAT = "userInfo:uid_%d";

    /**
     * 房间详情
     */
    public static final String ROOM_INFO_FORMAT = "roomInfo:roomId_%d";

    /**
     * 群组详情
     */
    public static final String GROUP_INFO_FORMAT = "groupInfo:roomId_%d";

    /**
     * 用户token存放
     */
    public static final String USER_TOKEN_FORMAT = "userToken:%s:uid_%d";

	/**
	 * 用户refreshToken存放
	 */
	public static final String USER_REFRESH_TOKEN_FORMAT = "userRefreshToken:%s:uid_%d";

    /**
     * 用户的信息更新时间
     */
    public static final String USER_MODIFY_FORMAT = "userModify:uid_%d";

    /**
     * 用户的信息汇总
     */
    public static final String USER_SUMMARY_FORMAT = "userSummary:uid_%d";

    /**
     * 用户GPT聊天次数
     */
    public static final String USER_CHAT_NUM = "useChatGPTNum:uid_%d";

    public static final String USER_CHAT_CONTEXT = "useChatGPTContext:uid_%d_roomId_%d";

    /**
     * 保存Open id
     */
    public static final String OPEN_ID_FORMAT = "openid:%s";


    /**
     * 用户上次使用GLM使用时间
     */
    public static final String USER_GLM2_TIME_LAST = "userGLM2UseTime:uid_%d";

    /** 获取key */
    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }
}
