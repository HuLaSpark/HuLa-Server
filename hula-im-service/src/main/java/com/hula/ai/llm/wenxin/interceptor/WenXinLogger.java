package com.hula.ai.llm.wenxin.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class WenXinLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        log.info("文心一言>>>:{}", message);
    }

}
