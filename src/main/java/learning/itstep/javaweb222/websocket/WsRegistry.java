package learning.itstep.javaweb222.websocket;

import jakarta.websocket.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//аналог SignalR Groups
public class WsRegistry {

    // userId -> sessions
    private static final Map<String, Set<Session>> users =
            new ConcurrentHashMap<>();

    // chatId -> sessions
    private static final Map<String, Set<Session>> chats =
            new ConcurrentHashMap<>();

    public static void addUser(String userId, Session s) {
        users.computeIfAbsent(userId,
                k -> ConcurrentHashMap.newKeySet()).add(s);
    }

    public static void removeUser(String userId, Session s) {
        Set<Session> set = users.get(userId);
        if (set != null) {
            set.remove(s);
            if (set.isEmpty()) users.remove(userId);
        }
    }

    public static void joinChat(String chatId, Session s) {
        chats.computeIfAbsent(chatId,
                k -> ConcurrentHashMap.newKeySet()).add(s);
    }

    public static void leaveChat(String chatId, Session s) {
        Set<Session> set = chats.get(chatId);
        if (set != null) {
            set.remove(s);
            if (set.isEmpty()) chats.remove(chatId);
        }
    }

    public static void sendToChat(String chatId, String json) {
        Set<Session> set = chats.get(chatId);
        if (set != null) {
            set.forEach(s -> s.getAsyncRemote().sendText(json));
        }
    }
}
