package com.ganesh.chatbot.controller;

import com.ganesh.chatbot.dto.ChatHistoryDTO;
import com.ganesh.chatbot.dto.ChatRequestDTO;
import com.ganesh.chatbot.dto.ChatResponseDTO;
import com.ganesh.chatbot.service.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Allow frontend calls
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    /**
     * POST /api/chat/ask
     * Main endpoint — student sends a question, AI replies.
     *
     * Request body:
     * {
     *   "sessionId": "abc-123",
     *   "message": "What is the Pythagorean theorem?",
     *   "studentName": "Ganesh",
     *   "topic": "Mathematics"
     * }
     */
    @PostMapping("/ask")
    public ResponseEntity<ChatResponseDTO> ask(@Valid @RequestBody ChatRequestDTO request) {
        log.info("Received question from session: {} | Topic: {}", request.getSessionId(), request.getTopic());
        ChatResponseDTO response = chatService.chat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/chat/history/{sessionId}
     * Returns full chat history for a session.
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ChatHistoryDTO> getHistory(@PathVariable String sessionId) {
        log.info("Fetching history for session: {}", sessionId);
        ChatHistoryDTO history = chatService.getChatHistory(sessionId);
        return ResponseEntity.ok(history);
    }

    /**
     * DELETE /api/chat/clear/{sessionId}
     * Clears all chat history for a session.
     */
    @DeleteMapping("/clear/{sessionId}")
    public ResponseEntity<Map<String, String>> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.ok(Map.of(
                "message", "Session cleared successfully",
                "sessionId", sessionId
        ));
    }

    /**
     * GET /api/chat/new-session
     * Generates a new unique session ID for a student.
     */
    @GetMapping("/new-session")
    public ResponseEntity<Map<String, String>> newSession(
            @RequestParam(required = false) String studentName) {
        String sessionId = UUID.randomUUID().toString();
        return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,
                "studentName", studentName != null ? studentName : "Guest",
                "message", "New session created. Start asking questions!"
        ));
    }

    /**
     * GET /api/chat/health
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Student AI Chatbot",
                "version", "1.0.0"
        ));
    }
}
