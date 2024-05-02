package com.hula.common.interceptor;

import com.hula.common.exception.HttpErrorEnum;
import com.hula.core.user.service.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.Optional;

/**
 * @author nyh
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";
    @Resource
    private LoginService loginService;

    /**
     * 前置拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            request.setAttribute(ATTRIBUTE_UID, validUid);
        } else {
            boolean isPublicURI = isPublicURI(request);
            if (!isPublicURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;
    }

    private boolean isPublicURI(HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        String[] split = requestURL.split("/");
        return split.length > 2 && "public".equals(split[2]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }
}
