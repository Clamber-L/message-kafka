package com.tr.message.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author lez
 * @className NettyServerInitializer
 * @date 2026/5/9 0009 15:45
 * @description TODO
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;
    private final boolean compression;

    public NettyServerInitializer(SslContext sslContext, boolean compression) {
        this.sslContext = sslContext;
        this.compression = compression;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL（wss需要）
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }

        // HTTP 编解码
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));

        // 压缩（对应 useCompressionHandler = "true"）
        if (compression) {
            pipeline.addLast(new WebSocketServerCompressionHandler(1024));
        }

        // 心跳检测，读超时 60s
        pipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));

        // 业务处理
        pipeline.addLast(new WebSocketServer());
    }
}
