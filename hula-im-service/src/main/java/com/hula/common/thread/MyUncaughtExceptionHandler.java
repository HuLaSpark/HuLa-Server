package com.hula.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nyh
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread", e);
    }
}
