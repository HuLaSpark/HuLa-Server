package com.hula.core.websocket.domain.service;

import com.hula.core.websocket.domain.enums.WSBaseResp;
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

    void authorize(Channel channel, String token);

    void sendMsgToAll(WSBaseResp<?> msg);
}