package com.luohuo.basic.boot.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.base.R;
import com.luohuo.basic.exception.ArgumentException;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.ForbiddenException;
import com.luohuo.basic.exception.TokenExceedException;
import com.luohuo.basic.exception.UnauthorizedException;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.utils.StrPool;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.luohuo.basic.exception.code.ResponseEnum.METHOD_NOT_ALLOWED;
import static com.luohuo.basic.exception.code.ResponseEnum.REQUIRED_FILE_PARAM_EX;


/**
 * 全局异常统一处理
 *
 * @author 乾乾
 * @date 2017-12-13 17:04
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
@Slf4j
public abstract class AbstractGlobalExceptionHandler {
    @Value("${spring.profiles.active:dev}")
    protected String profiles;

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<?> bizException(BizException ex) {
        log.warn("BizException:", ex);
        return R.result(ex.getCode(), null, ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(SaTokenException.class)
    public R<?> handlerSaTokenException(SaTokenException e) {
        log.warn("SaTokenException:", e);
        return R.result(ResponseEnum.JWT_TOKEN_EXCEED.getCode(), null, ResponseEnum.JWT_TOKEN_EXCEED.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(NotLoginException.class)
    public R<?> handlerNotLoginException(NotLoginException nle) {

        // 打印堆栈，以供调试
        nle.printStackTrace();

        // 判断场景值，定制化异常信息
        String message;
		int code = ResponseEnum.JWT_TOKEN_EXCEED.getCode();
        if (nle.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未能读取到有效 token";
        } else if (nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "token 无效";
        } else if (nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token 已过期";
        } else if (nle.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "token 已被顶下线";
        } else if (nle.getType().equals(NotLoginException.KICK_OUT)) {
            message = "token 已被踢下线";
        } else if (nle.getType().equals(NotLoginException.TOKEN_FREEZE)) {
            message = "token 已被冻结";
        } else if (nle.getType().equals(NotLoginException.NO_PREFIX)) {
            message = "未按照指定前缀提交 token";
        } else {
            message = "当前会话未登录";
        }

        return R.result(code, null, message).setPath(getPath());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> forbiddenException(ForbiddenException ex) {
        log.warn("BizException:", ex);
        return R.result(ex.getCode(), null, ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> unauthorizedException(UnauthorizedException ex) {
        log.warn("BizException:", ex);
        return R.result(ex.getCode(), null, ex.getMessage()).setPath(getPath());
    }

	@ExceptionHandler(ArgumentException.class)
	@ResponseStatus(HttpStatus.OK)
	public R<?> unauthorizedException(ArgumentException ex) {
		log.warn("ArgumentException:", ex);
		return R.result(ex.getCode(), null, ex.getMessage()).setPath(getPath());
	}

    @ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("HttpMessageNotReadableException:", ex);
        String message = ex.getMessage();
        if (StrUtil.containsAny(message, "Could not read document:")) {
            String msg = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, "Could not read document:", " at "));
            return R.result(ResponseEnum.PARAM_EX.getCode(), null, msg).setPath(getPath());
        }
        return R.result(ResponseEnum.PARAM_EX.getCode(), null, ResponseEnum.PARAM_EX.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> bindException(BindException ex) {
        log.warn("BindException:", ex);
        try {
            String msg = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msg)) {
                return R.result(ResponseEnum.PARAM_EX.getCode(), null, msg).setPath(getPath());
            }
        } catch (Exception ee) {
            log.debug("获取异常描述失败", ee);
        }
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = ex.getFieldErrors();
        fieldErrors.forEach((oe) ->
                msg.append("参数:[").append(oe.getObjectName())
                        .append(".").append(oe.getField())
                        .append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
        );
        return R.result(ResponseEnum.PARAM_EX.getCode(), null, msg.toString()).setPath(getPath());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException:", ex);
        String msg = "参数：[" + ex.getName() + "]的传入值：[" + ex.getValue() +
                     "]与预期的字段类型：[" + Objects.requireNonNull(ex.getRequiredType()).getName() + "]不匹配";
        return R.result(ResponseEnum.PARAM_EX.getCode(), null, msg).setPath(getPath());
    }

    @ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> illegalStateException(IllegalStateException ex) {
        log.warn("IllegalStateException:", ex);
        return R.result(ResponseEnum.ILLEGAL_ARGUMENT_EX.getCode(), null, ResponseEnum.ILLEGAL_ARGUMENT_EX.getMsg()).setPath(getPath());
    }

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.OK)
	public R<?> illegalArgumentException(IllegalArgumentException ex) {
		log.warn("IllegalArgumentException:", ex);
		return R.result(ResponseEnum.ILLEGAL_ARGUMENT_EX.getCode(), null, ResponseEnum.ILLEGAL_ARGUMENT_EX.getMsg()).setPath(getPath());
	}

    @ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("MissingServletRequestParameterException:", ex);
        return R.result(ResponseEnum.ILLEGAL_ARGUMENT_EX.getCode(), null, "缺少必须的[" + ex.getParameterType() + "]类型的参数[" + ex.getParameterName() + "]").setPath(getPath());
    }

    @ExceptionHandler(NullPointerException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> nullPointerException(NullPointerException ex) {
        log.warn("NullPointerException:", ex);
        return R.result(ResponseEnum.NULL_POINT_EX.getCode(), null, ResponseEnum.NULL_POINT_EX.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("HttpMediaTypeNotSupportedException:", ex);
        MediaType contentType = ex.getContentType();
        if (contentType != null) {
            return R.result(ResponseEnum.MEDIA_TYPE_EX.getCode(), null, "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配").setPath(getPath());
        }
        return R.result(ResponseEnum.MEDIA_TYPE_EX.getCode(), null, "无效的Content-Type类型").setPath(getPath());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> missingServletRequestPartException(MissingServletRequestPartException ex) {
        log.warn("MissingServletRequestPartException:", ex);
        return R.result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(ServletException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> servletException(ServletException ex) {
        log.warn("ServletException:", ex);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(ex.getMessage())) {
            return R.result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg());
        }
        return R.result(ResponseEnum.SYSTEM_BUSY.getCode(), null, ResponseEnum.INTERNAL_SERVER_ERROR.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(MultipartException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> multipartException(MultipartException ex) {
        log.warn("MultipartException:", ex);
        return R.result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg()).setPath(getPath());
    }

    /**
     * jsr 规范中的验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> constraintViolationException(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));

        return R.result(ResponseEnum.BASE_VALID_PARAM.getCode(), null, message).setPath(getPath());
    }

    /**
     * spring 封装的参数验证异常， 在controller中没有写result参数时，会进入
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException:", ex);
        return R.result(ResponseEnum.BASE_VALID_PARAM.getCode(), null, Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage()).setPath(getPath());
    }

    /**
     * 其他异常
     *
     * @param ex 异常
     */
    @ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
    public R<?> otherExceptionHandler(Exception ex) {
        log.warn("Exception:", ex);
        if (ex.getCause() instanceof BizException cause) {
            return this.bizException(cause);
        }
        return R.result(ResponseEnum.SYSTEM_BUSY.getCode(), null, ResponseEnum.SYSTEM_BUSY.getMsg()).setPath(getPath());
    }

	/**
	 * token过期异常
	 * @param ex 异常
	 */
	@ExceptionHandler(TokenExceedException.class)
	@ResponseStatus(HttpStatus.OK)
	public R<?> tokenExceedExceptionHandler(TokenExceedException ex) {
		log.warn("Exception:", ex);
		return R.result(ex.getCode(), null, ex.getMessage()).setPath(getPath());
	}

    /**
     * 返回状态码:405
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
	@ResponseStatus(HttpStatus.OK)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("HttpRequestMethodNotSupportedException:", ex);
        return R.result(METHOD_NOT_ALLOWED.getCode(), null, METHOD_NOT_ALLOWED.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<?> persistenceException(PersistenceException ex) {
        log.warn("PersistenceException:", ex);
        if (ex.getCause() instanceof BizException cause) {
            return R.result(cause.getCode(), null, cause.getMessage());
        }
        return R.result(ResponseEnum.SQL_EX.getCode(), null, ResponseEnum.SQL_EX.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<?> myBatisSystemException(MyBatisSystemException ex) {
        log.warn("PersistenceException:", ex);
        if (ex.getCause() instanceof PersistenceException cause) {
            return this.persistenceException(cause);
        }
        return R.result(ResponseEnum.SQL_EX.getCode(), null, ResponseEnum.SQL_EX.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<?> sqlException(SQLException ex) {
        log.warn("SQLException:", ex);
        return R.result(ResponseEnum.SQL_EX.getCode(), null, ResponseEnum.SQL_EX.getMsg()).setPath(getPath());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<?> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("DataIntegrityViolationException:", ex);
        return R.result(ResponseEnum.SQL_EX.getCode(), null, ResponseEnum.SQL_EX.getMsg()).setPath(getPath());
    }

    private String getPath() {
        String path = StrPool.EMPTY;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            path = request.getRequestURI();
        }
        return path;
    }
}
