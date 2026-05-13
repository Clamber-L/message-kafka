package com.tr.message.websocket;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

@Slf4j
@Component
public class WebSocketHandler {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 添加连接
     */
    public void put(Session session) {
        channelGroup.add(session.channel());
    }

    /**
     * 移除连接
     *
     * @return 移除结果
     */
    public static void remove(Session session) {
        if (session.isOpen()) {
            session.close();
        }

        channelGroup.remove(session.channel());
    }

    /**
     * 发送文本消息
     *
     * @param session 自己的用户名
     * @param message 消息内容
     */
    public static void sendMessageToUserByText(Session session, String message) {
        if (session != null) {
            session.sendText(message);
        } else {
            log.info("\n[你已离线]");
        }
    }

    /***
     * 群发消息
     */
    public void sendMessageToMass(String message) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }

}
