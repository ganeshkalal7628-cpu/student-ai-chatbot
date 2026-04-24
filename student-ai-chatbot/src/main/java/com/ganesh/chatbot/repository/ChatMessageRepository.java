package com.ganesh.chatbot.repository;

import com.ganesh.chatbot.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Get all messages for a session ordered by time
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    // Get last N messages for context window
    @Query(value = "SELECT * FROM chat_messages WHERE session_id = :sessionId " +
                   "ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ChatMessage> findLastNMessagesBySessionId(
            @Param("sessionId") String sessionId,
            @Param("limit") int limit);

    // Count messages per session
    long countBySessionId(String sessionId);

    // Get messages by topic
    List<ChatMessage> findBySessionIdAndTopicOrderByCreatedAtAsc(String sessionId, String topic);

    // Delete all messages for a session
    void deleteBySessionId(String sessionId);
}
