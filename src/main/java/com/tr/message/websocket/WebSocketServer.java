package com.tr.message.websocket;

import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

@Slf4j
@Component
@ServerEndpoint(path = "/ws/iot", host = "0.0.0.0", port = "8181", useCompressionHandler = "true", childOptionSoKeepalive = "true")
public class WebSocketServer {

    @OnOpen
    public void onOpen(Session session) {

        WebSocketHandler webSocketHandler = new WebSocketHandler();
        webSocketHandler.put(session);
        log.info("WebSocket连接成功 --->  已连接, 总连接数:{}", WebSocketHandler.channelGroup.size());
        session.sendText("WEBSOCKET-CONNECT-SUCCESS");
    }

    @OnClose
    public void onClose(Session session) {
        if (session.isOpen()) {
            // 关闭连接
            session.close();
        }

        WebSocketHandler.remove(session);
        log.warn("WebSocket关闭连接 ---> , 总连接数:{}", WebSocketHandler.channelGroup.size());
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        if (session.isOpen()) {
            // 关闭连接
            session.close();
        }
        if (exception != null) {
            log.warn("WebSocket连接异常 --->  连接异常 异常信息 - {}", exception.getMessage());
        }

        WebSocketHandler.remove(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        //消息回传，保持连接
        if ("ping".equalsIgnoreCase(message)) {
            WebSocketHandler.sendMessageToUserByText(session, "pong");
        }
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            log.debug("onBinary : {}", String.valueOf(b));
        }
        session.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(Session session, Object evt, @RequestParam String userId) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    log.error("user-{} Read timeout！", userId);
                    session.close();
                    break;
                case WRITER_IDLE:
                    log.error("user-{} Write timeout！", userId);
                    session.close();
                    break;
                case ALL_IDLE:
                    log.error("user-{} All timeout！", userId);
                    session.close();
                    break;
                default:
                    break;
            }
        }
    }
}
