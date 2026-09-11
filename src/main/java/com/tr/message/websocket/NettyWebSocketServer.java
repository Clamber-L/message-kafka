package com.tr.message.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author lez
 * @className NettyWebSocketServer
 * @date 2026/5/9 0009 15:44
 * @description TODO
 */
@Slf4j
@Component
//@Profile({"prod", "test", "local", "dev", "jifa"})
public class NettyWebSocketServer implements ApplicationRunner, DisposableBean {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Value("${netty.websocket.host:0.0.0.0}")
    private String host;

    @Value("${netty.websocket.port:8181}")
    private int port;

    @Value("${netty.websocket.so-keepalive:true}")
    private boolean soKeepalive;

    @Value("${netty.websocket.compression:true}")
    private boolean compression;

    @Value("${netty.websocket.ssl.enabled:false}")
    private boolean sslEnabled;

    @Value("${netty.websocket.ssl.cert-path:}")
    private String certPath;

    @Value("${netty.websocket.ssl.key-path:}")
    private String keyPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        SslContext sslContext = buildSslContext();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, soKeepalive)
                .childHandler(new NettyServerInitializer(sslContext, compression));

        ChannelFuture future = bootstrap.bind(host, port).sync();
        log.info("Netty WebSocket 启动成功 ---> port:{}, ssl:{}", port, sslEnabled);
        future.channel().closeFuture().addListener(f -> log.info("Netty WebSocket 已停止"));
    }

    private SslContext buildSslContext() throws Exception {
        if (!sslEnabled) {
            return null;
        }
        return SslContextBuilder
                .forServer(new File(certPath), new File(keyPath))
                .build();
    }

    @Override
    public void destroy() {
        log.info("Netty WebSocket 正在关闭...");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}