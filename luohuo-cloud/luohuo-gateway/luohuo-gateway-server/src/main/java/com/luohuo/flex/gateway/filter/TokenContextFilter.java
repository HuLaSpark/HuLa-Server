package com.luohuo.flex.gateway.filter;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.flex.common.utils.IPUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextConstants;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.UnauthorizedException;
import com.luohuo.basic.utils.StrPool;
import com.luohuo.flex.common.properties.IgnoreProperties;

import static com.luohuo.basic.context.ContextConstants.*;

/**
 * 过滤器
 *
 * @author 乾乾
 * @date 2019/07/31
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TokenContextFilter implements WebFilter, Ordered {
    private final IgnoreProperties ignoreProperties;
    protected final SaTokenConfig saTokenConfig;

    @Value("${spring.profiles.active:dev}")
    protected String profiles;

    protected boolean isDev(String token) {
        return !StrPool.PROD.equalsIgnoreCase(profiles) && (StrPool.TEST_TOKEN.equalsIgnoreCase(token) || StrPool.TEST.equalsIgnoreCase(token));
    }

    @Override
    public int getOrder() {
        return OrderedConstant.TOKEN;
    }


    /**
     * 忽略 用户token
     */
    protected boolean isIgnoreToken(ServerHttpRequest request) {
        return ignoreProperties.isIgnoreUser(request.getMethod().name(), request.getPath().toString());
    }

    /**
     * 忽略 租户编码
     */
    protected boolean isIgnoreTenant(ServerHttpRequest request) {
        return ignoreProperties.isIgnoreTenant(request.getMethod().name(), request.getPath().toString());
    }

    protected String getHeader(String headerName, ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String token = StrUtil.EMPTY;
        if (headers == null || headers.isEmpty()) {
            return token;
        }

        token = headers.getFirst(headerName);

        if (StrUtil.isNotBlank(token)) {
            return token;
        }

        return request.getQueryParams().getFirst(headerName);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = request.mutate();
		mutate.header(HEADER_REQUEST_IP, IPUtils.getClientIp(request));
        ContextUtil.setGrayVersion(getHeader(ContextConstants.GRAY_VERSION, request));

        try {
            // 1 获取 应用信息
            parseApplication(request, mutate);

            Mono<Void> token = parseToken(exchange, chain, mutate);
            if (token != null) {
                return token;
            }
        } catch (UnauthorizedException e) {
            return errorResponse(response, e.getMessage(), e.getCode());
        } catch (BizException e) {
            return errorResponse(response, e.getMessage(), e.getCode());
        } catch (SaTokenException e) {
            log.error(e.getMessage(), e);
            return errorResponse(response, ResponseEnum.JWT_TOKEN_EXCEED.getMsg(), ResponseEnum.JWT_TOKEN_EXCEED.getCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return errorResponse(response, "验证token出错", R.FAIL_CODE);
        }

        ServerHttpRequest build = mutate.build();
        return chain.filter(exchange.mutate().request(build).build()).doFinally(e -> {
			ContextUtil.remove();
			ContextUtil.clearTenantContext();
		});
    }

    private Mono<Void> parseToken(ServerWebExchange exchange, WebFilterChain chain, ServerHttpRequest.Builder mutate) {
        ServerHttpRequest request = exchange.getRequest();
        // 判断接口是否需要忽略token验证
        if (isIgnoreToken(request)) {
            log.debug("当前接口：{}, 不解析用户token", request.getPath());
			return chain.filter(exchange.mutate().request(mutate.build()).build());
        }

        HttpHeaders headers = request.getHeaders();

        String tokenName = saTokenConfig.getTokenName();
        String token = headers.getFirst(tokenName);
        // 如果请求头中没有token，则尝试从URL参数中获取
        if (StrUtil.isBlank(token)) {
            token = request.getQueryParams().getFirst(tokenName);
        }

        SaSession tokenSession = StpUtil.getTokenSessionByToken(token);
        log.info("{}", tokenSession);

        if (tokenSession != null) {
            Long userId = (Long) tokenSession.getLoginId();
            long topCompanyId = tokenSession.getLong(JWT_KEY_TOP_COMPANY_ID);
            long companyId = tokenSession.getLong(JWT_KEY_COMPANY_ID);
            long deptId = tokenSession.getLong(JWT_KEY_DEPT_ID);
            long uid = tokenSession.getLong(JWT_KEY_U_ID);
            long tenantId = tokenSession.getLong(HEADER_TENANT_ID);

			mutate.header(JWT_KEY_SYSTEM_TYPE, tokenSession.getString(JWT_KEY_SYSTEM_TYPE));
			mutate.header(USER_ID_HEADER, String.valueOf(userId));
            mutate.header(U_ID_HEADER, String.valueOf(uid));
            mutate.header(CURRENT_TOP_COMPANY_ID_HEADER, String.valueOf(topCompanyId));
            mutate.header(CURRENT_COMPANY_ID_HEADER, String.valueOf(companyId));
            mutate.header(CURRENT_DEPT_ID_HEADER, String.valueOf(deptId));
            mutate.header(HEADER_TENANT_ID, String.valueOf(tenantId));
        }

        return null;
    }

    private void parseApplication(ServerHttpRequest request, ServerHttpRequest.Builder mutate) {
        String applicationIdStr = getHeader(APPLICATION_ID_KEY, request);
        if (StrUtil.isNotEmpty(applicationIdStr)) {
            ContextUtil.setApplicationId(applicationIdStr);
            addHeader(mutate, APPLICATION_ID_HEADER, ContextUtil.getApplicationId());
            MDC.put(APPLICATION_ID_HEADER, applicationIdStr);
        }
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = URLUtil.encode(valueStr);
        mutate.header(name, valueEncode);
    }

    protected Mono<Void> errorResponse(ServerHttpResponse response, String errMsg, int errCode) {
        R tokenError = R.fail(errCode, errMsg);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatusCode(HttpStatus.OK);
        DataBuffer dataBuffer = response.bufferFactory().wrap(tokenError.toString().getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

}
