package com.hula.ai.llm.moonshot.interceptor;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MoonshotInterceptor implements Interceptor {

    public MoonshotInterceptor() {
    }
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();
        Request request = original.newBuilder()
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
