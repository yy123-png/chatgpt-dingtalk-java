package com.yy.chatgpt.openai;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.yy.chatgpt.common.CommonConstant;
import com.yy.chatgpt.common.RoleEnum;
import com.yy.chatgpt.openai.request.ChatMessage;
import com.yy.chatgpt.openai.request.ChatRequest;
import com.yy.chatgpt.openai.response.ChatResponse;
import com.yy.chatgpt.user.UserSession;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author yeyu
 * @since 2023-03-03 14:48
 */
@Slf4j
public class ChatGPTOperation {

    private String apiKey;

    private Integer maxTokens = 512;

    private String model = "gpt-3.5-turbo";

    private Double temperature = 0.9;

    private String clearSessionToken = "清空会话";


    private void loadConfig() {

    }

    private String extractPostResult(String postResult) {
        try {
            ChatResponse chatResponse = JSON.parseObject(postResult, ChatResponse.class);
            List<ChatResponse.Choice> choices = chatResponse.getChoices();
            if (CollUtil.isEmpty(choices)) {
                log.error("ChatGPT请求成功,响应内容为空");
                return null;
            }
            return choices.get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("解析ChatGPT结果异常:{}", postResult);
        }
        return null;
    }

    public ChatGPTOperation() {
        this.loadConfig();
    }

    public String makeReply(String userId, String content, RoleEnum roleEnum) {
        ChatMessage userContent = new ChatMessage();
        userContent.setContent(content);
        userContent.setRole(roleEnum.getCode());
        List<ChatMessage> messages = UserSession.getMessages(userId);
        messages.add(userContent);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMax_tokens(maxTokens);
        chatRequest.setTemperature(temperature);
        chatRequest.setModel(model);
        chatRequest.setMessages(messages);


        String postResult = HttpUtil.createPost(CommonConstant.CHAT_API)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(JSON.toJSONString(chatRequest))
                .execute()
                .body();

        String reply = this.extractPostResult(postResult);
        ChatMessage aiContent = new ChatMessage();
        aiContent.setContent(reply);
        aiContent.setRole(RoleEnum.ASSISTANT.getCode());

        UserSession.putMessage(userId, userContent);
        UserSession.putMessage(userId, aiContent);

        return reply;
    }


}
