package com.luohuo.basic.boot.utils;

import cn.hutool.core.util.NumberUtil;
import com.luohuo.basic.base.R;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.luohuo.basic.context.ContextConstants.HEADER_TENANT_ID;
import static com.luohuo.basic.context.ContextConstants.HEADER_VISIT_TENANT_ID;

public class WebFrameworkUtils {

    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id";
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_TYPE = "login_user_type";

    private static final String REQUEST_ATTRIBUTE_COMMON_RESULT = "common_result";

    /**
     * 终端的 Header
     */
    public static final String HEADER_TERMINAL = "terminal";

//    private static WebProperties properties;
//
//    public WebFrameworkUtils(WebProperties webProperties) {
//        WebFrameworkUtils.properties = webProperties;
//    }

    /**
     * 获得租户编号，从 header 中
     * 考虑到其它 framework 组件也会使用到租户编号，所以不得不放在 WebFrameworkUtils 统一提供
     *
     * @param request 请求
     * @return 租户编号
     */
    public static Long getTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(HEADER_TENANT_ID);
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    /**
     * 获得访问的租户编号，从 header 中
     * 考虑到其它 framework 组件也会使用到租户编号，所以不得不放在 WebFrameworkUtils 统一提供
     *
     * @param request 请求
     * @return 租户编号
     */
    public static Long getVisitTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(HEADER_VISIT_TENANT_ID);
        return NumberUtil.isNumber(tenantId)? Long.valueOf(tenantId) : null;
    }

    public static void setLoginUserId(ServletRequest request, Long userId) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID, userId);
    }

    /**
     * 设置用户类型
     *
     * @param request 请求
     * @param userType 用户类型
     */
    public static void setLoginUserType(ServletRequest request, Integer userType) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE, userType);
    }

    /**
     * 获得当前用户的编号，从请求中
     * 注意：该方法仅限于 framework 框架使用！！！
     *
     * @param request 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return (Long) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID);
    }

//    /**
//     * 获得当前用户的类型
//     * 注意：该方法仅限于 web 相关的 framework 组件使用！！！
//     *
//     * @param request 请求
//     * @return 用户编号
//     */
//    public static Integer getLoginUserType(HttpServletRequest request) {
//        if (request == null) {
//            return null;
//        }
//        // 1. 优先，从 Attribute 中获取
//        Integer userType = (Integer) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE);
//        if (userType != null) {
//            return userType;
//        }
//        // 2. 其次，基于 URL 前缀的约定
//        if (request.getServletPath().startsWith(properties.getAdminApi().getPrefix())) {
//            return UserTypeEnum.ADMIN.getValue();
//        }
//        if (request.getServletPath().startsWith(properties.getAppApi().getPrefix())) {
//            return UserTypeEnum.MEMBER.getValue();
//        }
//        return null;
//    }
//
//    public static Integer getLoginUserType() {
//        HttpServletRequest request = getRequest();
//        return getLoginUserType(request);
//    }

    public static Long getLoginUserId() {
        HttpServletRequest request = getRequest();
        return getLoginUserId(request);
    }

//    public static Integer getTerminal() {
//        HttpServletRequest request = getRequest();
//        if (request == null) {
//            return TerminalEnum.UNKNOWN.getTerminal();
//        }
//        String terminalValue = request.getHeader(HEADER_TERMINAL);
//        return NumberUtil.parseInt(terminalValue, TerminalEnum.UNKNOWN.getTerminal());
//    }

    public static void setCommonResult(ServletRequest request, R<?> result) {
        request.setAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT, result);
    }

    public static R<?> getCommonResult(ServletRequest request) {
        return (R<?>) request.getAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT);
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

//    /**
//     * 判断是否为 RPC 请求
//     *
//     * @param request 请求
//     * @return 是否为 RPC 请求
//     */
//    public static boolean isRpcRequest(HttpServletRequest request) {
//        return request.getRequestURI().startsWith(RpcConstants.RPC_API_PREFIX);
//    }

    /**
     * 判断是否为 RPC 请求
     *
     * 约定大于配置，只要以 Api 结尾，都认为是 RPC 接口
     *
     * @param className 类名
     * @return 是否为 RPC 请求
     */
    public static boolean isRpcRequest(String className) {
        return className.endsWith("Api");
    }

}
