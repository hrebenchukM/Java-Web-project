package learning.itstep.javaweb222.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.Map;

@ServerEndpoint(
    value = "/ws/chat",
    configurator = WsAuthConfigurator.class
)
public class ChatEndpoint {

    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String userId = (String)
            config.getUserProperties().get("userId");

        session.getUserProperties().put("userId", userId);
        WsRegistry.addUser(userId, session);
    }

    @OnMessage
    public void onMessage(String json, Session session) {
        Map<String, Object> msg = gson.fromJson(json, Map.class);
        String type = (String) msg.get("type");

        if ("join".equals(type)) {
            String chatId = (String) msg.get("chatId");
            WsRegistry.joinChat(chatId, session);
        }

        else if ("leave".equals(type)) {
            String chatId = (String) msg.get("chatId");
            WsRegistry.leaveChat(chatId, session);
        }

        else if ("typing".equals(type)) {
            String chatId = (String) msg.get("chatId");
            WsRegistry.sendToChat(chatId, json);
        }

        else if ("message".equals(type)) {
            String chatId = (String) msg.get("chatId");
            WsRegistry.sendToChat(chatId, json);
        }
    }

    @OnClose
    public void onClose(Session session) {
        String userId =
            (String) session.getUserProperties().get("userId");

        WsRegistry.removeUser(userId, session);
    }
}
