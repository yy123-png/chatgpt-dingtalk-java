package com.yy.chatgpt.openai.request;

import lombok.Data;

/**
 * @author yeyu
 * @since 2023-03-03 16:42
 */
@Data
public class ChatMessage {

    private String role;

    private String content;
}
