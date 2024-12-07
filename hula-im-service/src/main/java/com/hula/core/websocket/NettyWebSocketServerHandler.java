package com.hula.core.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.hula.core.user.domain.enums.WSReqTypeEnum;
import com.hula.core.user.domain.vo.req.ws.WSAuthorize;
import com.hula.core.user.domain.vo.req.ws.WSBaseReq;
import com.hula.core.user.service.WebSocketService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * @author nyh
 */
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    /**
     *  当web客户端连接后，触发该方法
     *
     * @param ctx 通道上下文
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        if(Objects.isNull(webSocketService)) {
            webSocketService = SpringUtil.getBean(WebSocketService.class);
        }
    }

    /**
     * 通道无效
     *
     * @param ctx 通道上下文
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("[{}]掉线", ctx.channel().id());
        userOffline(ctx);
    }

    /**
     * 心跳检查
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffline(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            webSocketService.connect(ctx.channel());
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StrUtil.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), new WSAuthorize(token));
            }
            log.info("握手成功：{}", (Long)NettyUtil.getAttr(ctx.channel(), NettyUtil.UID));
        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息", cause);
        ctx.channel().close();
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                webSocketService.handleLogin(ctx.channel());
                log.info("请求二维码 = {}", msg.text());
                break;
            case HEARTBEAT:
                log.info("{},{}",NettyUtil.getAttr(ctx.channel(), NettyUtil.IP), "心跳检测");
                break;
            default:
                log.info("未知类型");
        }
    }

    private void userOffline(ChannelHandlerContext ctx) {
        webSocketService.remove(ctx.channel());
        ctx.channel().close();
    }

}
