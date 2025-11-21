package com.luohuo.basic.cloud.http;

import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.luohuo.basic.context.ContextUtil;

import java.io.IOException;
import java.util.Map;

/**
 * 通过 RestTemplate 调用时，传递请求头和线程变量
 *
 * @author 乾乾
 */
@AllArgsConstructor
@Slf4j
public class RestTemplateHeaderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes,
                                        ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders httpHeaders = request.getHeaders();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            Map<String, String> localMap = ContextUtil.getLocalMap();
            localMap.forEach(httpHeaders::add);
            return execution.execute(request, bytes);
        }

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (httpServletRequest == null) {
            log.warn("path={}, 在FeignClient API接口未配置FeignConfiguration类， 故而无法在远程调用时获取请求头中的参数!", request.getURI());
            return execution.execute(request, bytes);
        }

        // 传递请求头
        HttpHeaders headers = request.getHeaders();
        headers.forEach((key, values) -> {
            String value = httpServletRequest.getHeader(key);
            value = ObjectUtil.isEmpty(value) ? ContextUtil.get(key) : value;
            httpHeaders.add(key, value);
        });

        return execution.execute(request, bytes);
    }
}
