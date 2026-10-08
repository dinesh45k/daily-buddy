package com.lifeassistant.chat;

import com.lifeassistant.ai.ClaudeClient;
import com.lifeassistant.ai.GeminiClient;
import com.lifeassistant.config.AiProperties;
import com.lifeassistant.task.Task;
import com.lifeassistant.task.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final String BASE_PROMPT = """
            You are a friendly, practical personal life assistant. Help the user plan their day, \
            prioritise tasks, build habits and make decisions. Keep answers short and actionable.""";

    private final ClaudeClient claude;
    private final GeminiClient gemini;
    private final TaskRepository tasks;
    private final AiProperties props;
    private final LocalAssistant local;

    public ChatService(ClaudeClient claude, GeminiClient gemini, TaskRepository tasks,
                       AiProperties props, LocalAssistant local) {
        this.claude = claude;
        this.gemini = gemini;
        this.tasks = tasks;
        this.props = props;
        this.local = local;
    }

    public String chat(ChatRequest request) {
        List<Task> open = tasks.findByDoneFalseOrderByDueDateAsc();
        boolean hasClaude = hasText(props.apiKey());
        boolean hasGemini = hasText(props.geminiApiKey());

        // No key configured: answer with the built-in offline assistant.
        if (!hasClaude && !hasGemini) {
            return local.reply(request.message(), open);
        }

        List<Map<String, String>> turns = new ArrayList<>();
        if (request.history() != null) {
            for (ChatRequest.Turn t : request.history()) {
                if (t != null && t.content() != null && !t.content().isBlank()
                        && ("user".equals(t.role()) || "assistant".equals(t.role()))) {
                    turns.add(Map.of("role", t.role(), "content", t.content()));
                }
            }
        }
        turns.add(Map.of("role", "user", "content", request.message()));

        String system = buildSystemPrompt(open);
        return hasClaude ? claude.complete(system, turns) : gemini.complete(system, turns);
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private String buildSystemPrompt(List<Task> open) {
        if (open.isEmpty()) {
            return BASE_PROMPT + "\n\nThe user has no open tasks right now.";
        }
        String list = open.stream()
                .map(t -> "- " + t.getTitle() + (t.getDueDate() != null ? " (due " + t.getDueDate() + ")" : ""))
                .collect(Collectors.joining("\n"));
        return BASE_PROMPT + "\n\nThe user's open tasks:\n" + list;
    }
}
