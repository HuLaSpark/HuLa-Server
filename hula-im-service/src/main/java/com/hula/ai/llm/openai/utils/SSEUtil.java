package com.hula.ai.llm.openai.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hula.ai.llm.openai.exception.BaseException;
import com.hula.ai.llm.openai.exception.CommonError;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.Objects;

/**
 * 描述：
 */
@Slf4j
public class SSEUtil {

    /**
     * 执行get sse请求
     *
     * @param okHttpClient
     * @param url
     * @param eventSourceListener
     * @param <T>
     */
    public static <T> void get(OkHttpClient okHttpClient, String url, EventSourceListener eventSourceListener) {
        get(okHttpClient, url, Headers.of(MapUtil.empty()), eventSourceListener);
    }

    /**
     * 执行get sse请求
     *
     * @param okHttpClient
     * @param url
     * @param headers
     * @param eventSourceListener
     */
    public static void get(OkHttpClient okHttpClient, String url, Headers headers, EventSourceListener eventSourceListener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(headers)
                .build();
        get(okHttpClient, request, eventSourceListener);
    }

    /**
     * 执行get sse请求
     *
     * @param okHttpClient
     * @param request
     * @param eventSourceListener
     * @param <T>
     */
    public static <T> void get(OkHttpClient okHttpClient, Request request, EventSourceListener eventSourceListener) {
        execute(okHttpClient, request, eventSourceListener);
    }


    /**
     * 执行post sse请求
     *
     * @param okHttpClient
     * @param url
     * @param headers
     * @param body
     * @param eventSourceListener
     * @param <T>
     */
    public static <T> void post(OkHttpClient okHttpClient, String url, Headers headers, T body, EventSourceListener eventSourceListener) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(body);
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .build();
            execute(okHttpClient, request, eventSourceListener);
        } catch (JsonProcessingException e) {
            log.error("请求参数解析异常：{}", e);
            e.printStackTrace();
        }
    }

    /**
     * 执行post sse请求
     *
     * @param okHttpClient
     * @param url
     * @param body
     * @param eventSourceListener
     * @param <T>
     */
    public static <T> void post(OkHttpClient okHttpClient, String url, T body, EventSourceListener eventSourceListener) {
        post(okHttpClient, url, Headers.of(MapUtil.empty()), body, eventSourceListener);
    }

    /**
     * 执行sse请求
     *
     * @param okHttpClient
     * @param request
     * @param eventSourceListener
     * @param <T>
     */
    public static <T> void execute(OkHttpClient okHttpClient, Request request, EventSourceListener eventSourceListener) {
        if (Objects.isNull(eventSourceListener)) {
            log.error("参数异常：EventSourceListener不能为空，可以参考：com.master.chat.llm.openai.sse.ConsoleEventSourceListener");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        try {
            EventSource.Factory factory = EventSources.createFactory(okHttpClient);
            //创建事件
            factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常：{}", e);
            e.printStackTrace();
        }
    }
}
