package com.hula.ai.llm.base.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * websocket处理器
 *
 * @author: 云裂痕
 * @date: 2023/03/08
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Slf4j
@ServerEndpoint("/v1/chat/websocket/{uuid}")
@Component
public class WebsocketServer {

    /**
     * 用户数量,用来记录当前在线连接数
     */
    private static AtomicInteger onlineNum = new AtomicInteger();

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static Map<String, Session> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 建立连接
     *
     * @param session session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uuid") String uuid) {
        int num = onlineNum.get();
        if (!webSocketMap.containsKey(uuid)) {
            num = onlineNum.incrementAndGet();
        }
        webSocketMap.put(uuid, session);
        log.info("新建立客户端连接，sessionId为：{}，用户uuid为：{}，当前连接数为：{}", session.getId(), uuid, num);
    }

    /**
     * 关闭连接
     *
     * @param session session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("sessionId为{}的客户端连接关闭，open：{}", session.getId(), session.isOpen());
        if (session == null) {
            return;
        }
        webSocketMap.values().remove(session);
        int num = onlineNum.decrementAndGet();
        log.info("sessionId为{}的客户端连接关闭，当前剩余连接数为：{}", session.getId(), num);
    }

    /**
     * 发生错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误：{}，Session ID： {}，open：{}", error, session.getId(), session.isOpen());
        if (session == null) {
            return;
        }
        webSocketMap.values().remove(session);
        int num = onlineNum.decrementAndGet();
        log.info("sessionId为{}的客户端连接关闭，当前剩余连接数为：{}", session.getId(), num);
    }

    /**
     * 客户端发送消息
     *
     * @param session session
     * @param message 消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        sendMessage(session, message);
        log.info("sessionId : {} , onMessage : {}", session.getId(), message);
    }

    /**
     * 服务器端推送消息
     *
     * @param session session
     * @param message 消息
     */
    public static void sendMessage(Session session, String message) {
        try {
            if (session == null || !session.isOpen()) {
                log.error("WebSocket发送消息出错：session信息不存在或者已关闭");
                return;
            }
            // 同步消息
            session.getBasicRemote().sendText(message);
            // 异步消息
            //session.getAsyncRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WebSocket发送消息出错：{}", e.getMessage());
        }
    }

    /**
     * 服务器端推送消息
     *
     * @param message 消息
     */
    public static void sendMessageByUserId(String uuid, String message) {
        Session session = webSocketMap.get(uuid);
        if (session == null || !session.isOpen()) {
            log.error("WebSocket发送消息出错：session信息不存在或者已关闭，用户uuid为：{}" + uuid);
            return;
        }
        sendMessage(session, message);
    }

}
