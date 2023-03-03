package com.yy.chatgpt.openai.response;

import com.yy.chatgpt.openai.request.ChatMessage;
import lombok.Data;

import java.util.List;

/**
 * @author yeyu
 * @since 2023-03-03 17:10
 */
@Data
public class ChatResponse {

    private String id;

    private String object;

    private Long created;

    private List<Choice> choices;

    private Usage usage;

    @Data
    public static class Choice {
        private Integer index;

        private ChatMessage message;

        private String finish_reason;
    }

    @Data
    public static class Usage {

        private Integer prompt_tokens;

        private Integer completion_tokens;

        private Integer total_tokens;
    }
}
