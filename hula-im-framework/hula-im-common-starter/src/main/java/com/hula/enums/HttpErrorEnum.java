package com.hula.enums;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.hula.domain.vo.res.ApiResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Description: 业务校验异常码
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum {
    ACCESS_DENIED(401, "登录失效，请重新登录"),
	BLACK_ERROR(360, "对不起，不在白名单内"),
	RATE_LIMIT_EXCEEDED(1002, "请求频率过高，请稍后再试"),
	SYSTEM_ERROR(1001, "系统出小差了，请稍后再试哦~~"),
    BIZ_ERROR(2001, "业务异常，请稍后再试哦~~"),
	FREQUENCY_LIMIT(-4, "请求太频繁了，请稍后再试哦~~"),
	JWT_TOKEN_EXCEED(40004, "token已过期"),
    ;
    private Integer code;
    private String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }

    public void sendHttpError(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.OK.value());
        ApiResult<Object> responseData = ApiResult.tokenExceed(this);
        response.setContentType(ContentType.JSON.toString(Charset.forName("UTF-8")));
        response.getWriter().write(JSONUtil.toJsonStr(responseData));
    }
}
