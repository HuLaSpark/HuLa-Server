package com.luohuo.flex.gateway.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.flex.im.facade.DefResourceFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import com.luohuo.basic.base.R;
import com.luohuo.flex.common.properties.IgnoreProperties;
import com.luohuo.flex.model.vo.result.ResourceApiVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.luohuo.basic.context.ContextConstants.JWT_KEY_SYSTEM_TYPE;

/**
 *注册 Sa-Token全局过滤器
 * @author tangyh
 * @since 2024/8/6 16:33
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationSaInterceptor implements WebFilter, Ordered {
    private final DefResourceFacade defResourceFacade;
    private final IgnoreProperties ignoreProperties;

    @Override
    public int getOrder() {
        return OrderedConstant.AUTHENTICATION;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 写入WebFilterChain对象
        exchange.getAttributes().put(SaReactorHolder.EXCHANGE_KEY, chain);

        // ---------- 全局认证处理 仅后台需要鉴权
        if("1".equals(exchange.getRequest().getHeaders().getFirst(JWT_KEY_SYSTEM_TYPE))){
			try {
				// 写入全局上下文 (同步)
				SaReactorSyncHolder.setContext(exchange);

				// 执行全局过滤器

				Map<String, Set<String>> anyUser = ignoreProperties.buildAnyUser();
				// 验证token 排除掉需要租户ID，但不需要登录
				SaRouter
						.match("/**")    // 拦截的 path 列表，可以写多个 */
						.notMatch(r -> {
							String path = SaHolder.getRequest().getRequestPath();
							String method = SaHolder.getRequest().getMethod();
							for (Map.Entry<String, Set<String>> map : anyUser.entrySet()) {
								String key = map.getKey();
								Set<String> value = map.getValue();
								if (StrUtil.equalsAny(key, method, SaHttpMethod.ALL.name())) {
									for (String ignore : value) {
										if (StrUtil.equals(ignore, path)) {
											return true;
										}

										if (SaPathPatternParserUtil.match(ignore, path)) {
											return true;
										}
									}
								}
							}
							return false;
						})
						.check(r -> StpUtil.checkLogin());

				// 无需校验权限
				if (!ignoreProperties.getAuthEnabled()) {
					return chain.filter(exchange).contextWrite(ctx -> {
						ctx = ctx.put(SaReactorHolder.EXCHANGE_KEY, exchange);
						return ctx;
					}).doFinally(r -> {
						SaReactorSyncHolder.clearContext();
					});
				}

				// 接口权限
				Map<String, Set<String>> anyone = ignoreProperties.buildAnyone();
				Map<String, Set<String>> allApi = this.defResourceFacade.listAllApi();

				allApi.forEach((api, auth) -> {
					List<String> list = StrUtil.split(api, "###");
					String uri = list.get(0);
					String requestMethod = list.get(1);
					SaRouter.match(uri).matchMethod(requestMethod)
							.notMatch(r -> {
								String path = SaHolder.getRequest().getRequestPath();
								String method = SaHolder.getRequest().getMethod();
								for (Map.Entry<String, Set<String>> map : anyone.entrySet()) {
									String key = map.getKey();
									Set<String> value = map.getValue();
									if (StrUtil.equalsAny(key, method, SaHttpMethod.ALL.name())) {
										for (String ignore : value) {
											if (StrUtil.equals(ignore, path)) {
												return true;
											}

											if (SaPathPatternParserUtil.match(ignore, path)) {
												return true;
											}
										}
									}
								}
								return false;
							})
							.check(r -> StpUtil.checkPermissionOr(auth.toArray(String[]::new)));
				});


				if (!ignoreProperties.getNotConfigUriAllow()) {
					String path = SaHolder.getRequest().getRequestPath();
					String method = SaHolder.getRequest().getMethod();
					ResourceApiVO resourceApi = new ResourceApiVO();
					resourceApi.setUri(path);
					resourceApi.setRequestMethod(method);


					if (!ignoreProperties.isIgnoreAnyone(method, path)) {
						boolean flag = false;
						for (Map.Entry<String, Set<String>> map : allApi.entrySet()) {
							List<String> list = StrUtil.split(map.getKey(), "###");
							String uri = list.get(0);
							String requestMethod = list.get(1);

							if (StrUtil.equalsAny(requestMethod, method, SaHttpMethod.ALL.name())) {
								if (StrUtil.equals(uri, path) || SaPathPatternParserUtil.match(uri, path)) {
									flag = true;
								}
							}
						}

						if (!flag) {
							throw new NotPermissionException(resourceApi.getUri(), StpUtil.TYPE).setCode(SaErrorCode.CODE_11051);
						}
					}
				}

			} catch (StopMatchException e) {
				// StopMatchException 异常代表：停止匹配，进入Controller
			} catch (Throwable e) {
				// 1. 获取异常处理策略结果
				String result = e.getMessage();
				ServerHttpResponse response = exchange.getResponse();
				R tokenError = R.fail(ResponseEnum.JWT_TOKEN_EXCEED.getCode(), result);
				response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
				response.setStatusCode(HttpStatus.OK);
				DataBuffer dataBuffer = response.bufferFactory().wrap(tokenError.toString().getBytes());
				return response.writeWith(Mono.just(dataBuffer));
			} finally {
				// 清除上下文
				SaReactorSyncHolder.clearContext();
			}

			// ---------- 执行

			// 写入全局上下文 (同步)
			SaReactorSyncHolder.setContext(exchange);
		}

        // 执行
        return chain.filter(exchange).contextWrite(ctx -> {
            // 写入全局上下文 (异步)
            ctx = ctx.put(SaReactorHolder.EXCHANGE_KEY, exchange);
            return ctx;
        }).doFinally(r -> {
            // 清除上下文
            SaReactorSyncHolder.clearContext();
        });
    }
}
