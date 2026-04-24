package com.ganesh.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// ── Chat Response DTO ─────────────────────────────────────────────────────────
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDTO {
    private String sessionId;
    private String reply;
    private String topic;
    private LocalDateTime timestamp;
    private boolean success;
    private String errorMessage;
}
