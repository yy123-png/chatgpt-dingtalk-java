package com.yy.chatgpt.openai.request;

import lombok.Data;

import java.util.List;

/**
 * @author yeyu
 * @since 2023-03-03 16:41
 */
@Data
public class ChatRequest {
    private String model;

    private Double temperature;

    private Integer max_tokens;

    private List<ChatMessage> messages;
}
