package com.hula.core.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.hula.core.websocket.domain.enums.WSReqTypeEnum;
import com.hula.core.websocket.domain.service.WebSocketService;
import com.hula.core.websocket.domain.vo.req.WSBaseReq;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * @author nyh
 */
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffline(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StrUtil.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), token);
            }
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.channel().close();
                userOffline(ctx.channel());
            }
        }
    }

    /**
     * 用户下线统一处理
     **/
    private void userOffline(Channel channel) {
        webSocketService.remove(channel);
        channel.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case AUTHORIZE:
                webSocketService.authorize(ctx.channel(), wsBaseReq.getData());
            case HEARTBEAT:
                break;
            case LOGIN:
                webSocketService.handleLoginReq(ctx.channel());
        }
    }
}
