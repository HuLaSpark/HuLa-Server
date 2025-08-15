package com.luohuo.flex.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.luohuo.basic.base.R;
import com.luohuo.basic.exception.*;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import static com.luohuo.basic.exception.code.ResponseEnum.METHOD_NOT_ALLOWED;

/**
 * 全局异常处理
 *
 * @author 乾乾
 * @date 2025年06月19日10:19:27
 */
@Slf4j
public abstract class WebFluxGlobalExceptionHandler implements ErrorWebExceptionHandler {

	@Value("${spring.profiles.active:dev}")
	protected String profiles;

	private List<HttpMessageReader<?>> messageReaders;
	private List<HttpMessageWriter<?>> messageWriters;

	public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
		this.messageReaders = messageReaders;
	}

	public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
		this.messageWriters = messageWriters;
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		R<?> result = resolveException(exchange, ex);
		return writeResponse(exchange, result);
	}

	private R<?> resolveException(ServerWebExchange exchange, Throwable ex) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().value();

		if (ex instanceof BizException) {
			return handleBizException((BizException) ex, path);
		} else if (ex instanceof TokenExceedException) {
			return tokenExceedExceptionHandler((TokenExceedException) ex);
		} else if (ex instanceof SaTokenException) {
			return handleSaTokenException((SaTokenException) ex, path);
		} else if (ex instanceof NotLoginException) {
			return handleNotLoginException((NotLoginException) ex, path);
		} else if (ex instanceof ArgumentException) {
			return handleArgumentException((ArgumentException) ex, path);
		} else if (ex instanceof ForbiddenException) {
			return handleForbiddenException((ForbiddenException) ex, path);
		} else if (ex instanceof UnauthorizedException) {
			return handleUnauthorizedException((UnauthorizedException) ex, path);
		} else if (ex instanceof ServerWebInputException) {
			return handleServerWebInputException((ServerWebInputException) ex, path);
		} else if (ex instanceof WebExchangeBindException) {
			return handleWebExchangeBindException((WebExchangeBindException) ex, path);
		} else if (ex instanceof MethodNotAllowedException) {
			return handleMethodNotAllowedException((MethodNotAllowedException) ex, path);
		} else if (ex instanceof NotAcceptableStatusException) {
			return handleNotAcceptableStatusException((NotAcceptableStatusException) ex, path);
		} else if (ex instanceof UnsupportedMediaTypeStatusException) {
			return handleUnsupportedMediaTypeStatusException((UnsupportedMediaTypeStatusException) ex, path);
		} else if (ex instanceof ServerErrorException) {
			return handleServerErrorException((ServerErrorException) ex, path);
		} else if (ex instanceof ResponseStatusException) {
			return handleResponseStatusException((ResponseStatusException) ex, path);
		}
		return handleDefaultException(ex, path);
	}

	private R<?> handleBizException(BizException ex, String path) {
		log.warn("BizException:", ex);
		return buildResult(ex.getCode(), ex.getMessage(), path);
	}

	/**
	 * token过期异常
	 * @param ex 异常
	 */
	private R<?> tokenExceedExceptionHandler(TokenExceedException ex) {
		log.warn("TokenExceedException:", ex);
		return buildResult(ex.getCode(), ex.getMessage(), null);
	}

	private R<?> handleSaTokenException(SaTokenException e, String path) {
		log.warn("SaTokenException:", e);
		return buildResult(e.getCode(), e.getMessage(), path);
	}

	private R<?> handleNotLoginException(NotLoginException nle, String path) {
		log.warn("NotLoginException:", nle);
		String message = switch (nle.getType()) {
			case NotLoginException.NOT_TOKEN -> "未能读取到有效 token";
			case NotLoginException.INVALID_TOKEN -> "token 无效";
			case NotLoginException.TOKEN_TIMEOUT -> "token 已过期";
			case NotLoginException.BE_REPLACED -> "token 已被顶下线";
			case NotLoginException.KICK_OUT -> "token 已被踢下线";
			case NotLoginException.TOKEN_FREEZE -> "token 已被冻结";
			case NotLoginException.NO_PREFIX -> "未按照指定前缀提交 token";
			default -> "当前会话未登录";
		};
		return buildResult(nle.getCode(), message, path);
	}

	private R<?> handleArgumentException(ArgumentException ex, String path) {
		log.warn("ArgumentException:", ex);
		return buildResult(ex.getCode(), ex.getMessage(), path);
	}

	private R<?> handleForbiddenException(ForbiddenException ex, String path) {
		log.warn("ForbiddenException:", ex);
		return buildResult(ex.getCode(), ex.getMessage(), path);
	}

	private R<?> handleUnauthorizedException(UnauthorizedException ex, String path) {
		log.warn("UnauthorizedException:", ex);
		return buildResult(ex.getCode(), ex.getMessage(), path);
	}

	private R<?> handleServerWebInputException(ServerWebInputException ex, String path) {
		log.warn("ServerWebInputException:", ex);
		String msg = "参数错误: " + ex.getReason();
		return buildResult(ResponseEnum.PARAM_EX.getCode(), msg, path);
	}

	private R<?> handleWebExchangeBindException(WebExchangeBindException ex, String path) {
		log.warn("WebExchangeBindException:", ex);
		Optional<FieldError> fieldError = ex.getFieldErrors().stream().findFirst();
		if (fieldError.isPresent()) {
			String msg = fieldError.get().getDefaultMessage();
			if (StrUtil.isNotEmpty(msg)) {
				return buildResult(ResponseEnum.PARAM_EX.getCode(), msg, path);
			}
		}

		StringBuilder msg = new StringBuilder();
		ex.getFieldErrors().forEach(oe ->
				msg.append("参数:[").append(oe.getObjectName())
						.append(".").append(oe.getField())
						.append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
		);
		return buildResult(ResponseEnum.PARAM_EX.getCode(), msg.toString(), path);
	}

	private R<?> handleMethodNotAllowedException(MethodNotAllowedException ex, String path) {
		log.warn("MethodNotAllowedException:", ex);
		return buildResult(METHOD_NOT_ALLOWED.getCode(), METHOD_NOT_ALLOWED.getMsg(), path);
	}

	private R<?> handleUnsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException ex, String path) {
		log.warn("UnsupportedMediaTypeStatusException:", ex);
		String msg = "不支持的媒体类型: " + ex.getContentType();
		return buildResult(ResponseEnum.MEDIA_TYPE_EX.getCode(), msg, path);
	}

	private R<?> handleNotAcceptableStatusException(NotAcceptableStatusException ex, String path) {
		log.warn("NotAcceptableStatusException:", ex);
		return buildResult(ResponseEnum.MEDIA_TYPE_EX.getCode(), "不可接受的媒体类型", path);
	}

	private R<?> handleServerErrorException(ServerErrorException ex, String path) {
		log.warn("ServerErrorException:", ex);
		return buildResult(ResponseEnum.SYSTEM_BUSY.getCode(), "服务器内部错误", path);
	}

	private R<?> handleResponseStatusException(ResponseStatusException ex, String path) {
		log.warn("ResponseStatusException:", ex);
		return buildResult(ex.getStatusCode().value(), ex.getReason(), path);
	}

	private R<?> handleDefaultException(Throwable ex, String path) {
		log.error("Unhandled Exception:", ex);
		return buildResult(ResponseEnum.SYSTEM_BUSY.getCode(),
				ResponseEnum.SYSTEM_BUSY.getMsg(), path);
	}

	private R<?> buildResult(int code, String msg, String path) {
		return R.result(code, null, msg)
				.setPath(path);
	}

	private String getErrorMsg(Throwable ex) {
		return StrPool.PROD.equals(profiles) ? StrPool.EMPTY : ex.getMessage();
	}

	private Mono<Void> writeResponse(ServerWebExchange exchange, R<?> result) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.OK);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		// 将响应对象转换为JSON字节数组
		byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = response.bufferFactory().wrap(bytes);

		return response.writeWith(Mono.just(buffer));
	}
}