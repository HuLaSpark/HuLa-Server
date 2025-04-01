package com.hula.ai.llm.internlm.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class InternlmLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        log.info("书生浦语>>>:{}", message);
    }

}
