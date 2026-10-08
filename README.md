# Daily Buddy

A Java 17 + Spring Boot app that combines a simple task manager with an AI chat assistant.
The assistant sees your open tasks, so it can help you plan and prioritise your day.

## Tech stack
- Java 17, Spring Boot 3.3 (Web, Data JPA, Validation)
- H2 file database (tasks persist in `./data`)
- Anthropic Messages API via Spring `RestClient`
- Plain HTML/JS front end (no build step)

## Run
```bash
export ANTHROPIC_API_KEY=your_key_here   # Windows PowerShell: $env:ANTHROPIC_API_KEY="your_key_here"
mvn spring-boot:run
```
Open http://localhost:8080

No API key? The chat still works in **offline mode** (built-in rules). Set `ANTHROPIC_API_KEY` (Claude) or `GEMINI_API_KEY` (Google Gemini, optional `GEMINI_MODEL`) to switch to a real AI.

## API
| Method | Path | Description |
|---|---|---|
| GET | `/api/tasks` | List tasks |
| POST | `/api/tasks` | Create task `{"title":"...","dueDate":"2026-10-10"}` |
| PATCH | `/api/tasks/{id}/toggle` | Mark done / undone |
| DELETE | `/api/tasks/{id}` | Delete task |
| POST | `/api/chat` | `{"message":"...","history":[{"role":"user","content":"..."}]}` |

## Test
```bash
mvn test
```

## Structure
```
src/main/java/com/lifeassistant
  LifeAssistantApplication.java
  config/   AiProperties
  ai/       ClaudeClient
  task/     Task, TaskRepository, TaskController
  chat/     ChatController, ChatService, ChatRequest, ChatResponse
```

## Ideas to extend
Habit tracker, daily summary endpoint, PostgreSQL profile, user login (Spring Security), Docker.
