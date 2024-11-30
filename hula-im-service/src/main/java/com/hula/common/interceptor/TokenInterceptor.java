package com.hula.common.interceptor;

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

    /**
     * 前置拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPublicURI(request)) {
            return true;
        }
        String token = getToken(request);
        if (!tokenService.verify(token)){
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

    private boolean isPublicURI(HttpServletRequest request) {
//        String requestURL = request.getRequestURI();
//        String[] split = requestURL.split("/");
//        return split.length > 3 && "public".equals(split[3]);
        return Objects.nonNull(publicUrlProperties) && StringUtils.equalsAny(request.getRequestURI(), publicUrlProperties.getUrls());
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }
}
