package com.hula.snowflake.uid.utils;

public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();

    /**
     * 将Long型ID编码为定长Base62字符串（可配置12-16位）
     */
    public static String encode(long id, int fixedLength) {
        StringBuilder sb = new StringBuilder();
        while (id > 0 && sb.length() < fixedLength) {
            sb.append(BASE62_CHARS.charAt((int)(id % BASE)));
            id /= BASE;
        }
        // 不足长度时左侧补零
        while (sb.length() < fixedLength) {
            sb.append('0');
        }
        return sb.reverse().toString();
    }
}