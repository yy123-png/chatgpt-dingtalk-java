package com.yy.chatgpt.user;

import cn.hutool.core.collection.CollUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yy.chatgpt.common.CustomConfig;
import com.yy.chatgpt.openai.request.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yeyu
 * @since 2023-03-03 10:53
 */
public class UserCache {

    private final Cache<String, List<ChatMessage>> cache;

    private final CustomConfig customConfig;

    public UserCache(CustomConfig customConfig) {
        this.customConfig = customConfig;
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(customConfig.getSessionTimeOut() * 2, TimeUnit.SECONDS)
                .expireAfterWrite(customConfig.getSessionTimeOut(), TimeUnit.SECONDS)
                .build();

    }

    public void putMessage(String userId, ChatMessage message) {
        List<ChatMessage> chatMessages = cache.getIfPresent(userId);
        if (CollUtil.isEmpty(chatMessages)) {
            chatMessages = new ArrayList<>();
        }
        chatMessages.add(message);
        cache.put(userId, chatMessages);

    }

    public List<ChatMessage> getMessages(String userId) {
        List<ChatMessage> chatMessages = cache.getIfPresent(userId);
        if (CollUtil.isEmpty(chatMessages)) {
            return new ArrayList<>();
        }
        return chatMessages;
    }

    public void clearSession(String userId) {
        cache.invalidate(userId);
    }

}
