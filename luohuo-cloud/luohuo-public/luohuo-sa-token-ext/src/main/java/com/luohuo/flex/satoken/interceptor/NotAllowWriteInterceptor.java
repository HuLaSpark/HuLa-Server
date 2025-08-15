package com.luohuo.flex.satoken.interceptor;

import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import cn.hutool.core.collection.CollUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.common.properties.SystemProperties;

import java.util.List;

/**
 * 禁止写入拦截器 [演示环境专用]
 * @author tangyh
 * @since 2024/8/9 15:41
 */
@Slf4j
@RequiredArgsConstructor
public class NotAllowWriteInterceptor implements AsyncHandlerInterceptor {
    private final SystemProperties systemProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 禁用该拦截器
        if (!systemProperties.getNotAllowWrite()) {
            return true;
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        List<String> list = systemProperties.getNotAllowWriteList().get(method);
        if (CollUtil.isNotEmpty(list)) {

            for (String url : list) {
                if (SaPathPatternParserUtil.match(url, path)) {
                    throw new BizException(-1, "演示环境禁止新增、修改、删除系统级数据！请创建其他租户账号后测试全部功能。");
                }
            }
        }

        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }

}
