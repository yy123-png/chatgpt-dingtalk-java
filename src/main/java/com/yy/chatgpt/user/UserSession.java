package com.yy.chatgpt.user;

import com.yy.chatgpt.openai.request.ChatMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yeyu
 * @since 2023-03-03 10:53
 */
public class UserSession {

    private static final Map<String, List<ChatMessage>> USER_SESSION = new ConcurrentHashMap<>(16);

    public static void putMessage(String userId, ChatMessage message) {
        List<ChatMessage> messageList = USER_SESSION.getOrDefault(userId, new ArrayList<>());
        messageList.add(message);
        USER_SESSION.put(userId, messageList);
    }

    public static List<ChatMessage> getMessages(String userId) {
        return USER_SESSION.getOrDefault(userId, new ArrayList<>());
    }

    public static void clearSession(String userId) {
        USER_SESSION.remove(userId);
    }

}
