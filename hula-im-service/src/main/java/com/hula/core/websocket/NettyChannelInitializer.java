package com.hula.core.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    public final static String WEB_SOCKET_PATH = "/websocket";

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 30秒客户端没有向服务器发送心跳则关闭连接
        pipeline.addLast(new IdleStateHandler(30, 0, 0));
        // 因为使用http协议，所以需要使用http的编码器，解码器
        pipeline.addLast(new HttpServerCodec());
        // 以块方式写，添加 chunkedWriter 处理器
        pipeline.addLast(new ChunkedWriteHandler());
        /*
          说明：
           1. http数据在传输过程中是分段的，HttpObjectAggregator可以把多个段聚合起来；
           2. 这就是为什么当浏览器发送大量数据时，就会发出多次 http请求的原因
         */
        pipeline.addLast(new HttpObjectAggregator(8192));
        // 保存用户ip
        pipeline.addLast(new HttpHeadersHandler());
        /*
          说明：
           1. 对于 WebSocket，它的数据是以帧frame 的形式传递的；
           2. 可以看到 WebSocketFrame 下面有6个子类
           3. 浏览器发送请求时： ws://localhost:7000/hello 表示请求的uri
           4. WebSocketServerProtocolHandler 核心功能是把 http协议升级为 ws 协议，保持长连接；
               是通过一个状态码 101 来切换的
         */
        // 添加 /websocket路由用于识别websocket请求, 方便在nginx中作localtion配置
        pipeline.addLast(new WebSocketServerProtocolHandler(WEB_SOCKET_PATH));
        // 自定义handler ，处理业务逻辑
        pipeline.addLast(new NettyWebSocketServerHandler());
    }
}
