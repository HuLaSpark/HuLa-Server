package com.hula.ai.llm.moonshot.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class MoonshotLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        log.info("月之暗面>>>:{}", message);
    }

}
