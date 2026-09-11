package com.tr.message.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketHandler {

    public static final ChannelGroup channelGroup =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 添加连接（对应原来的 put）
     */
    public void put(Channel channel) {
        channelGroup.add(channel);
    }

    /**
     * 移除连接（对应原来的 remove）
     */
    public static void remove(Channel channel) {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
        channelGroup.remove(channel);
    }

    /**
     * 发送文本消息给指定用户（对应原来的 sendMessageToUserByText）
     */
    public static void sendMessageToUserByText(Channel channel, String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
        } else {
            log.info("\n[你已离线]");
        }
    }

    /**
     * 群发消息（不变）
     */
    public void sendMessageToMass(String message) {
        log.info("[WEBSOCKET-PUSH-MESSAGE] <---> {}", message);
        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }
}