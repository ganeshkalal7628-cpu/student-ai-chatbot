package com.ganesh.chatbot.service;

import com.ganesh.chatbot.dto.ChatHistoryDTO;
import com.ganesh.chatbot.dto.ChatRequestDTO;
import com.ganesh.chatbot.dto.ChatResponseDTO;
import com.ganesh.chatbot.model.ChatMessage;
import com.ganesh.chatbot.model.ChatSession;
import com.ganesh.chatbot.repository.ChatMessageRepository;
import com.ganesh.chatbot.repository.ChatSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final int CONTEXT_WINDOW = 10; // Last 10 messages sent as context

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private OpenAiService openAiService;

    /**
     * Main method — processes student question and returns AI reply.
     */
    @Transactional
    public ChatResponseDTO chat(ChatRequestDTO request) {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(request.getSessionId());
        response.setTimestamp(LocalDateTime.now());

        try {
            // 1. Create or fetch session
            ChatSession session = getOrCreateSession(
                    request.getSessionId(),
                    request.getStudentName()
            );

            // 2. Save the student's message to DB
            ChatMessage userMessage = new ChatMessage();
            userMessage.setSessionId(request.getSessionId());
            userMessage.setStudentName(request.getStudentName());
            userMessage.setRole("user");
            userMessage.setMessage(request.getMessage());
            userMessage.setTopic(request.getTopic());
            chatMessageRepository.save(userMessage);

            // 3. Fetch last N messages as context for AI
            List<ChatMessage> history = chatMessageRepository
                    .findLastNMessagesBySessionId(request.getSessionId(), CONTEXT_WINDOW);
            // Reverse so oldest message is first (chronological order for AI)
            Collections.reverse(history);
            // Remove the message we just saved (it will be added as current question)
            if (!history.isEmpty() && history.get(history.size() - 1).getId().equals(userMessage.getId())) {
                history.remove(history.size() - 1);
            }

            // 4. Call OpenAI API
            String aiReply = openAiService.askQuestion(
                    request.getMessage(),
                    history,
                    request.getTopic()
            );

            // 5. Save AI reply to DB
            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setSessionId(request.getSessionId());
            aiMessage.setStudentName(request.getStudentName());
            aiMessage.setRole("assistant");
            aiMessage.setMessage(aiReply);
            aiMessage.setTopic(request.getTopic());
            chatMessageRepository.save(aiMessage);

            // 6. Update session stats
            session.setTotalMessages(session.getTotalMessages() + 2);
            chatSessionRepository.save(session);

            // 7. Build response
            response.setReply(aiReply);
            response.setTopic(request.getTopic());
            response.setSuccess(true);

            log.info("Chat completed for session: {}", request.getSessionId());

        } catch (Exception e) {
            log.error("Error processing chat for session {}: {}", request.getSessionId(), e.getMessage(), e);
            response.setSuccess(false);
            response.setReply("Sorry, I encountered an error. Please try again.");
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    /**
     * Returns full chat history for a session.
     */
    public ChatHistoryDTO getChatHistory(String sessionId) {
        List<ChatMessage> messages = chatMessageRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);

        ChatSession session = chatSessionRepository
                .findBySessionId(sessionId)
                .orElse(null);

        ChatHistoryDTO dto = new ChatHistoryDTO();
        dto.setSessionId(sessionId);
        dto.setStudentName(session != null ? session.getStudentName() : "Unknown");
        dto.setTotalMessages(messages.size());
        dto.setMessages(messages.stream().map(m -> {
            ChatHistoryDTO.MessageDTO msg = new ChatHistoryDTO.MessageDTO();
            msg.setRole(m.getRole());
            msg.setMessage(m.getMessage());
            msg.setTopic(m.getTopic());
            msg.setCreatedAt(m.getCreatedAt());
            return msg;
        }).collect(Collectors.toList()));

        return dto;
    }

    /**
     * Clears chat history for a session.
     */
    @Transactional
    public void clearSession(String sessionId) {
        chatMessageRepository.deleteBySessionId(sessionId);
        chatSessionRepository.findBySessionId(sessionId).ifPresent(s -> {
            s.setTotalMessages(0);
            chatSessionRepository.save(s);
        });
        log.info("Cleared session: {}", sessionId);
    }

    /**
     * Gets or creates a chat session.
     */
    private ChatSession getOrCreateSession(String sessionId, String studentName) {
        return chatSessionRepository.findBySessionId(sessionId).orElseGet(() -> {
            ChatSession newSession = new ChatSession();
            newSession.setSessionId(sessionId);
            newSession.setStudentName(studentName);
            newSession.setTotalMessages(0);
            log.info("Created new session: {}", sessionId);
            return chatSessionRepository.save(newSession);
        });
    }
}
