package com.ganesh.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// ── Chat Request DTO ──────────────────────────────────────────────────────────
@Data
public class ChatRequestDTO {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private String studentName;

    private String topic; // e.g. "Mathematics", "Science", "General"
}
