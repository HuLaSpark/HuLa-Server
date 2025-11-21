package com.luohuo.basic.boot.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.luohuo.basic.utils.StrPool;

import java.util.Objects;

/**
 * @author 乾乾
 * @date 2020年03月31日22:30:31
 */
public final class WebUtils {
    private WebUtils() {
    }

    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public static String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StrUtil.isEmpty(value)) {
            return StrPool.EMPTY;
        }
        return URLUtil.decode(value);
    }
}
