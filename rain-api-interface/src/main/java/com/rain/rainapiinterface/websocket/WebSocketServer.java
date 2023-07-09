package com.rain.rainapiinterface.websocket;

import com.google.gson.Gson;
import com.rain.rainapiinterface.model.dto.ArticleRequest;
import com.rain.rainapiinterface.publisher.RabbitMQPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/article/{sid}")
public class WebSocketServer {
    private final Gson gson = new Gson();

    private static Map<String, Session> sessionMap = new HashMap<>();
    @Resource
    private RabbitMQPublisher rabbitMQPublisher;

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        sessionMap.put(sid, session);
    }

    @OnMessage
    public void onMessage(String articleJson) {
        ArticleRequest articleRequest = gson.fromJson(articleJson, ArticleRequest.class);
        rabbitMQPublisher.publishTask(articleRequest);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        sessionMap.remove(sid);
    }
    public static void sendArticle(String article,String articleId){
        Session session = sessionMap.get(articleId);
        if (session == null){
            throw new RuntimeException("用户断开连接");
        }
        try {
            session.getBasicRemote().sendText(article);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
