package com.hula.common.websocket.domain.service;

import io.netty.channel.Channel;

/**
 * @author nyh
 */
public interface WebSocketService {
    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long uid);

    void waitAuthorize(Integer code);
}
