package com.ganesh.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryDTO {
    private String sessionId;
    private String studentName;
    private List<MessageDTO> messages;
    private int totalMessages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageDTO {
        private String role;
        private String message;
        private String topic;
        private LocalDateTime createdAt;
    }
}
