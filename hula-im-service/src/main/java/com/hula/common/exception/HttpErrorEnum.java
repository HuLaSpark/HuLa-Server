package com.hula.common.exception;

import cn.hutool.http.ContentType;
import com.hula.common.domain.vo.resp.ApiResult;
import com.hula.utils.JsonUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.Charsets;

import java.io.IOException;


/**
 * 业务校验异常码
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum {
    ACCESS_DENIED(401, "登录失效，请重新登录");

    private final Integer httpCode;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return httpCode;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(httpCode);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JsonUtils.toStr(ApiResult.fail(httpCode, msg)));
    }
}
