# 🤖 Student AI Chatbot
### Java Spring Boot + MySQL + OpenAI GPT

An AI-powered Q&A chatbot for students. Ask any academic question and get instant, intelligent answers. Built with **Spring Boot**, **MySQL**, and **OpenAI GPT API**.

---

## 🏗️ Project Structure

```
student-ai-chatbot/
├── src/main/java/com/ganesh/chatbot/
│   ├── StudentAiChatbotApplication.java   ← Main entry point
│   ├── controller/
│   │   └── ChatController.java            ← REST API endpoints
│   ├── service/
│   │   ├── ChatService.java               ← Business logic
│   │   └── OpenAiService.java             ← GenAI API integration
│   ├── model/
│   │   ├── ChatMessage.java               ← DB entity (messages)
│   │   └── ChatSession.java               ← DB entity (sessions)
│   ├── repository/
│   │   ├── ChatMessageRepository.java     ← JPA repository
│   │   └── ChatSessionRepository.java     ← JPA repository
│   ├── dto/
│   │   ├── ChatRequestDTO.java            ← Incoming request
│   │   ├── ChatResponseDTO.java           ← Outgoing response
│   │   └── ChatHistoryDTO.java            ← History response
│   └── config/
│       └── GlobalExceptionHandler.java    ← Error handling
├── src/main/resources/
│   ├── application.properties             ← Config (DB + OpenAI)
│   └── schema.sql                         ← MySQL schema
└── pom.xml                                ← Maven dependencies
```

---

## ⚙️ Setup Instructions

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- OpenAI API Key (get from https://platform.openai.com)

### 2. Database Setup
```sql
-- Run in MySQL Workbench or terminal
source src/main/resources/schema.sql
```

### 3. Configure application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_chatbot_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

openai.api.key=sk-YOUR_OPENAI_API_KEY
```

### 4. Run the Application
```bash
mvn spring-boot:run
```
App starts at: **http://localhost:8080**

---

## 📡 API Endpoints

### 1. Get New Session ID
```
GET /api/chat/new-session?studentName=Ganesh
```
Response:
```json
{
  "sessionId": "uuid-here",
  "studentName": "Ganesh",
  "message": "New session created. Start asking questions!"
}
```

---

### 2. Ask a Question ⭐ (Main Endpoint)
```
POST /api/chat/ask
Content-Type: application/json

{
  "sessionId": "uuid-here",
  "message": "What is the Pythagorean theorem?",
  "studentName": "Ganesh",
  "topic": "Mathematics"
}
```
Response:
```json
{
  "sessionId": "uuid-here",
  "reply": "The Pythagorean theorem states that in a right-angled triangle...",
  "topic": "Mathematics",
  "timestamp": "2025-01-15T10:30:00",
  "success": true
}
```

---

### 3. Get Chat History
```
GET /api/chat/history/{sessionId}
```

---

### 4. Clear Session
```
DELETE /api/chat/clear/{sessionId}
```

---

### 5. Health Check
```
GET /api/chat/health
```

---

## 💡 Supported Topics
- Mathematics
- Science (Physics, Chemistry, Biology)
- Computer Science
- History
- English
- General (any subject)

---

## 🛠️ Technologies Used
| Technology     | Purpose                        |
|----------------|--------------------------------|
| Java 17        | Programming language           |
| Spring Boot    | Backend framework              |
| Spring Data JPA| Database ORM                   |
| MySQL          | Data persistence               |
| OpenAI GPT API | AI response generation (GenAI) |
| OkHttp         | HTTP client for API calls      |
| Lombok         | Reduce boilerplate code        |
| Maven          | Build tool                     |

---

## 👨‍💻 Developer
**Ganesh Kalal** — ASP.NET & Java Full Stack Developer, Bangalore
