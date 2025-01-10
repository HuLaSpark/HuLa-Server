package com.hula.core.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import com.hula.utils.JwtUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @author nyh
 */
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            // 获取token参数
            String token = Optional.ofNullable(urlBuilder.getQuery()).map(k -> k.get("token")).map(CharSequence::toString).orElse(null);
            String clientId = Optional.ofNullable(urlBuilder.getQuery()).map(k -> k.get("clientId")).map(CharSequence::toString).orElse(null);
            if (StringUtils.isEmpty(clientId)) {
                ctx.fireChannelInactive();
                return;
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            NettyUtil.setAttr(ctx.channel(), NettyUtil.LOGIN_TYPE, JwtUtils.getLoginType(token));
            NettyUtil.setAttr(ctx.channel(), NettyUtil.CLIENT_ID, clientId);
            // 获取请求路径
            request.setUri(urlBuilder.getPath().toString());
            HttpHeaders headers = request.headers();
            String ip = headers.get("X-Real-IP");
            if (StringUtils.isEmpty(ip)) {
                // 如果没经过nginx，就直接获取远端地址
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}