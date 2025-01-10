package com.hula.core.websocket;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * netty工具类
 *
 * @author nyh
 */

public class NettyUtil {

    public final static String TOKEN = "token";
    public final static String IP = "ip";
    public final static String UID = "uid";
    public final static String LOGIN_TYPE = "loginType";
    public final static String CLIENT_ID = "clientId";

    public static <T> void setAttr(Channel channel, String key, T data) {
        channel.attr(AttributeKey.valueOf(key)).set(data);
    }

    public static <T> T getAttr(Channel channel, String key) {
        return channel.attr((AttributeKey<T>) AttributeKey.valueOf(key)).get();
    }
}
