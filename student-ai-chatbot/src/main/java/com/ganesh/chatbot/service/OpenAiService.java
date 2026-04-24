package com.ganesh.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ganesh.chatbot.model.ChatMessage;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAiService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max.tokens}")
    private int maxTokens;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * Sends a message to OpenAI with full conversation history for context.
     *
     * @param studentQuestion  The latest question from the student
     * @param conversationHistory  Previous messages for context (last 10)
     * @param topic  Subject topic e.g. "Mathematics"
     * @return AI-generated response string
     */
    public String askQuestion(String studentQuestion,
                               List<ChatMessage> conversationHistory,
                               String topic) throws Exception {

        // Build the messages array
        ArrayNode messages = objectMapper.createArrayNode();

        // 1. System prompt - defines AI behaviour as a student tutor
        ObjectNode systemMsg = objectMapper.createObjectNode();
        systemMsg.put("role", "system");
        systemMsg.put("content", buildSystemPrompt(topic));
        messages.add(systemMsg);

        // 2. Add conversation history for context (last 10 messages)
        for (ChatMessage history : conversationHistory) {
            ObjectNode histMsg = objectMapper.createObjectNode();
            histMsg.put("role", history.getRole()); // "user" or "assistant"
            histMsg.put("content", history.getMessage());
            messages.add(histMsg);
        }

        // 3. Add the current student question
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", studentQuestion);
        messages.add(userMsg);

        // Build request body
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.set("messages", messages);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", 0.7);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        log.debug("Sending request to OpenAI: {}", jsonBody);

        // HTTP POST to OpenAI
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                log.error("OpenAI API error: {} - {}", response.code(), errorBody);
                throw new RuntimeException("OpenAI API error: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            log.debug("OpenAI response: {}", responseBody);

            // Parse the response
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String aiReply = jsonResponse
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (aiReply == null || aiReply.isBlank()) {
                throw new RuntimeException("Empty response from OpenAI");
            }

            return aiReply.trim();
        }
    }

    /**
     * Builds a system prompt that makes the AI act as a helpful student tutor.
     */
    private String buildSystemPrompt(String topic) {
        String basePrompt = """
                You are EduBot, an intelligent and friendly AI tutor for students.
                Your role is to:
                - Answer student questions clearly and in simple language
                - Provide step-by-step explanations when needed
                - Give examples to make concepts easier to understand
                - Encourage students when they ask questions
                - If a question is outside your knowledge, politely say so
                - Keep responses concise but complete (max 3-4 paragraphs)
                - Always be positive, patient, and supportive
                - Do NOT answer questions unrelated to education or academics
                """;

        if (topic != null && !topic.isBlank() && !topic.equalsIgnoreCase("General")) {
            basePrompt += "\nThe student is currently studying: " + topic +
                    ". Focus your answers on this subject when relevant.";
        }

        return basePrompt;
    }
}
