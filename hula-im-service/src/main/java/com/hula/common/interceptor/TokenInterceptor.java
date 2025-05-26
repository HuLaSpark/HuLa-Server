package com.hula.common.interceptor;

import com.hula.ai.config.SecurityProperties;
import com.hula.common.config.PublicUrlProperties;
import com.hula.core.user.service.TokenService;
import com.hula.enums.HttpErrorEnum;
import com.hula.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.Optional;

/**
 * @author nyh
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(PublicUrlProperties.class)
public class TokenInterceptor implements HandlerInterceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";
    public static final String ATTRIBUTE_TOKEN = "token";
    private final PublicUrlProperties publicUrlProperties;
    private final TokenService tokenService;
    private final SecurityProperties securityProperties;

    /**
     * 前置拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPublicUri(request)) {
            return true;
        }
        String token = getToken(request);
        if (securityProperties.getMockEnable()){
            Long userId = mockLoginUser(token);
            request.setAttribute(ATTRIBUTE_UID, userId);
            return true;
        }
        if (!tokenService.verify(token)){
            HttpErrorEnum.JWT_TOKEN_EXCEED.sendHttpError(response);
            return false;
        }
        Long validUid = JwtUtils.getUidOrNull(token);
        if (Objects.nonNull(validUid)) {
            request.setAttribute(ATTRIBUTE_UID, validUid);
            request.setAttribute(ATTRIBUTE_TOKEN, token);
            return true;
        } else {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
    }

    private boolean isPublicUri(HttpServletRequest request) {
        return Objects.nonNull(publicUrlProperties) && StringUtils.equalsAny(request.getRequestURI(), publicUrlProperties.getUrls());
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }

    /**
     * 模拟登录用户，方便日常开发调试
     *
     * 注意，在线上环境下，一定要关闭该功能！！！
     *
     * @param token 模拟的 token，格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
     * @return 模拟的 LoginUser
     */
    private Long mockLoginUser(String token) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }
        // 构建模拟用户
        return Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
    }
}
