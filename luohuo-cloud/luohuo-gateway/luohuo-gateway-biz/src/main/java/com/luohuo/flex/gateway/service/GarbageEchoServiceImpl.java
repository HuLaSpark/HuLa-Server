package com.luohuo.flex.gateway.service;

import com.luohuo.basic.interfaces.echo.EchoService;

/**
 * 这个类仅仅是为了防止在gateway启动报错
 *
 * @author tangyh
 * @since 2024/8/8 23:06
 */
public class GarbageEchoServiceImpl implements EchoService {

    @Override
    public void action(Object obj, boolean isUseCache, String... ignoreFields) {

    }
}
