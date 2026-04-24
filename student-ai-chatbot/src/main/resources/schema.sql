-- ============================================================
-- Student AI Chatbot Database Schema
-- Run this before starting the application
-- ============================================================

CREATE DATABASE IF NOT EXISTS student_chatbot_db;
USE student_chatbot_db;

-- ── Chat Sessions Table ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS chat_sessions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id      VARCHAR(100) NOT NULL UNIQUE,
    student_name    VARCHAR(100),
    student_email   VARCHAR(150),
    total_messages  INT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_active_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id)
);

-- ── Chat Messages Table ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS chat_messages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id      VARCHAR(100) NOT NULL,
    student_name    VARCHAR(100),
    role            VARCHAR(20) NOT NULL COMMENT 'user or assistant',
    message         TEXT NOT NULL,
    topic           VARCHAR(100),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (session_id) REFERENCES chat_sessions(session_id) ON DELETE CASCADE
);

-- ── Sample Data for Testing ───────────────────────────────────
INSERT INTO chat_sessions (session_id, student_name, student_email, total_messages)
VALUES ('test-session-001', 'Ganesh Kalal', 'ganeshkalal7628@gmail.com', 0);
