package com.luohuo.basic.xss.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import com.luohuo.basic.xss.wrapper.XssRequestWrapper;

import java.io.IOException;
import java.util.List;

/**
 * 跨站工具 过滤器
 *
 * @author 乾乾
 * @date 2019-06-28 17:05
 */
@Slf4j
public class XssFilter implements Filter {

    /**
     * 可放行的请求路径
     */

    public static final String IGNORE_PATH = "ignorePath";
    /**
     * 可放行的参数值
     */
    public static final String IGNORE_PARAM_VALUE = "ignoreParamValue";
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 可放行的请求路径列表
     */
    private List<String> ignorePathList;
    /**
     * 可放行的参数值列表
     */
    private List<String> ignoreParamValueList;

    @Override
    public void init(FilterConfig fc) {
        this.ignorePathList = StrUtil.split(fc.getInitParameter(IGNORE_PATH), CharUtil.COMMA);
        this.ignoreParamValueList = StrUtil.split(fc.getInitParameter(IGNORE_PARAM_VALUE), CharUtil.COMMA);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 判断uri是否包含项目名称
        String uriPath = ((HttpServletRequest) request).getRequestURI();
        if (isIgnorePath(uriPath)) {
            log.debug("忽略过滤路径=[{}]", uriPath);
            chain.doFilter(request, response);
            return;
        }
        log.debug("过滤器包装请求路径=[{}]", uriPath);
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request, ignoreParamValueList), response);
    }

    private boolean isIgnorePath(String uriPath) {
        if (StrUtil.isBlank(uriPath)) {
            return true;
        }
        if (CollUtil.isEmpty(ignorePathList)) {
            return false;
        }
        return ignorePathList.stream().anyMatch(url -> uriPath.startsWith(url) || ANT_PATH_MATCHER.match(url, uriPath));
    }
}
