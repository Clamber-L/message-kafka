package com.tr.message.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public class WebSocketServer extends SimpleChannelInboundHandler<Object> {

    @Value("${netty.websocket.communication}")
    private String communication;

    @Value("${netty.websocket.path}")
    private String websocketPath;

    private final WebSocketHandler webSocketHandler = new WebSocketHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        webSocketHandler.put(ctx.channel());
        log.info("WebSocket连接成功, 总连接数:{}", WebSocketHandler.channelGroup.size());
        WebSocketHandler.sendMessageToUserByText(ctx.channel(), "WEBSOCKET-CONNECT-SUCCESS");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        WebSocketHandler.remove(ctx.channel());
        log.warn("WebSocket关闭连接, 总连接数:{}", WebSocketHandler.channelGroup.size());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("WebSocket连接异常: {}", cause.getMessage());
        WebSocketHandler.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest request) {
            handleHttpRequest(ctx, request);
        } else if (msg instanceof WebSocketFrame frame) {
            handleWebSocketFrame(ctx, frame);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                communication + "://" + request.headers().get(HttpHeaderNames.HOST) + websocketPath,
                null, true
        );
        WebSocketServerHandshaker handshaker = factory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame textFrame) {
            String message = textFrame.text();
            if ("ping".equals(message)) {
                WebSocketHandler.sendMessageToUserByText(ctx.channel(), "pong");
            }

        } else if (frame instanceof BinaryWebSocketFrame binaryFrame) {
            ByteBuf buf = binaryFrame.content().retain();
            ctx.channel().writeAndFlush(new BinaryWebSocketFrame(buf));

        } else if (frame instanceof CloseWebSocketFrame) {
            ctx.close();

        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent idleEvent) {
            switch (idleEvent.state()) {
                case READER_IDLE -> {
                    log.error("Read timeout, channel:{}", ctx.channel().id());
                    ctx.close();
                }
                case WRITER_IDLE -> {
                    log.error("Write timeout, channel:{}", ctx.channel().id());
                    ctx.close();
                }
                case ALL_IDLE -> {
                    log.error("All timeout, channel:{}", ctx.channel().id());
                    ctx.close();
                }
            }
        }
    }
}