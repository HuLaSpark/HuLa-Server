package com.luohuo.flex.common.cache;

/**
 * 缓存模块
 *
 * @author 乾乾
 * @date 2020/10/21
 */
public class CacheKeyModular {
    /**
     * 多个服务都会使用的缓存
     */
    public static final String COMMON = "common";
    /**
     * 仅基础服务base使用的缓存
     */
    public static final String BASE = "base";
    /**
     * 仅消息服务msg使用的缓存
     */
    public static final String MSG = "msg";
    /**
     * 仅认证服务oauth使用的缓存
     */
    public static final String OAUTH = "oauth";
	/**
	 * 聊天方面使用的缓存
	 */
	public static final String CHAT = "chat";
    /**
     * 仅文件服务file使用的缓存
     */
    public static final String FILE = "file";
	/**
	 * 仅在线用户服务presence使用的缓存
	 */
	public static final String PRESENCE = "presence";
	/**
	 * 好友的缓存
	 */
	public static final String FRIEND = "friend";
	/**
	 * 视频通话
	 */
	public static final String VideoCall = "VideoCall";
    /**
     * 仅租户服务tenant使用的缓存
     */
    public static final String SYSTEM = "system";
    /**
     * 仅网关服务gateway使用的缓存
     */
    public static final String GATEWAY = "gateway";

    /** 缓存key前缀， 可以在启动时覆盖该参数。
     * 系统启动时，注入。
     */
    public static String PREFIX;
}
