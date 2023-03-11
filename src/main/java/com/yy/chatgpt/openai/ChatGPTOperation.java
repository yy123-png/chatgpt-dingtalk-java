package com.yy.chatgpt.openai;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.yy.chatgpt.common.CommonConstant;
import com.yy.chatgpt.common.RoleEnum;
import com.yy.chatgpt.openai.request.ChatMessage;
import com.yy.chatgpt.openai.request.ChatRequest;
import com.yy.chatgpt.openai.request.ChatResponse;
import com.yy.chatgpt.user.UserContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author yeyu
 * @since 2023-03-03 14:48
 */
@Slf4j
public class ChatGPTOperation {

    private final UserContext userContext;

    public ChatGPTOperation(UserContext userContext) {
        this.userContext = userContext;
    }


    private String extractPostResult(String postResult) {
        try {
            ChatResponse chatResponse = JSON.parseObject(postResult, ChatResponse.class);
            List<ChatResponse.Choice> choices = chatResponse.getChoices();
            if (CollUtil.isEmpty(choices)) {
                throw new IllegalStateException("ChatGPT响应成功,请求结果为空");
            }
            return choices.get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("解析ChatGPT结果异常:{}", postResult);
            throw e;
        }
    }


    public RoleEnum getRole(String content) {
        if (CharSequenceUtil.isBlank(content)) {
            throw new IllegalArgumentException("请求内容为空");
        }
        content = content.trim();

        if (content.startsWith(userContext.getCustomConfig().getSystemToken())) {
            return RoleEnum.SYSTEM;
        }
        return RoleEnum.USER;
    }

    public String removeSystemToken(String content) {
        return content.replaceFirst(userContext.getCustomConfig().getSystemToken(), "");
    }

    public boolean clearSession(String userId, String content) {
        if (content.contains(userContext.getCustomConfig().getClearToken())) {
            userContext.getUserCache().clearSession(userId);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public String makeReply(String userId, String content, RoleEnum roleEnum) {

        ChatMessage userContent = new ChatMessage();
        userContent.setContent(content);
        userContent.setRole(roleEnum.getCode());
        List<ChatMessage> messages = userContext.getUserCache().getMessages(userId);
        messages.add(userContent);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMax_tokens(userContext.getCustomConfig().getMaxTokens());
        chatRequest.setTemperature(userContext.getCustomConfig().getTemperature());
        chatRequest.setModel(userContext.getCustomConfig().getModel());
        chatRequest.setMessages(messages);

        String postResult;
        if (Boolean.TRUE.equals(userContext.getCustomConfig().getUseHttpProxy())) {
            postResult = HttpUtil.createPost(CommonConstant.CHAT_API)
                    .timeout(60 * 1000)
                    .setHttpProxy("127.0.0.1", 7890)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + userContext.getCustomConfig().getApiKey())
                    .body(JSON.toJSONString(chatRequest))
                    .execute()
                    .body();
        } else {
            postResult = HttpUtil.createPost(CommonConstant.CHAT_API)
                    .timeout(60 * 1000)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + userContext.getCustomConfig().getApiKey())
                    .body(JSON.toJSONString(chatRequest))
                    .execute()
                    .body();
        }


        String reply = this.extractPostResult(postResult);
        ChatMessage aiContent = new ChatMessage();
        aiContent.setContent(reply.trim());
        aiContent.setRole(RoleEnum.ASSISTANT.getCode());

        userContext.getUserCache().putMessage(userId, aiContent);

        return reply;
    }


}
