package com.hula.ai.llm.locallm.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class LocalLMLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        log.info("LocalLM>>>:{}", message);
    }

}
