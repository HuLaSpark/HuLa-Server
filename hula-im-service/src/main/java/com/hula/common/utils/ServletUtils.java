package com.hula.common.utils;

import cn.hutool.extra.servlet.JakartaServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 客户端工具类
 * @author nyh
 */
@Component
public class ServletUtils extends JakartaServletUtil {

    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        try {
            return Objects.requireNonNull(getRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getClientIp() {
        return getClientIP(Objects.requireNonNull(getRequest()));
    }
}
