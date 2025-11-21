package com.luohuo.basic.log.monitor;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志埋点工具类
 *
 * @author 乾乾
 * @date 2020年03月09日18:16:16
 */
@Slf4j
public final class PointUtil {

    private static final String MSG_PATTERN = "{}|{}|{}";

    private PointUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 格式为：{对象id}|{类型}|{对象属性(以{@code &}分割)}
     * 例子1：12344|user-login|ip=xxx.xxx.xx {@code &} userName=张三 {@code &} userType=后台管理员
     * 例子2：12345|file-upload|fileName=xxx {@code &} filePath=xxx
     *
     * @param id      对象id
     * @param type    类型
     * @param message 对象属性
     */
    public static void info(String id, String type, String message) {
        log.info(MSG_PATTERN, id, type, message);
    }

    public static void debug(String id, String type, String message) {
        log.debug(MSG_PATTERN, id, type, message);
    }

}
