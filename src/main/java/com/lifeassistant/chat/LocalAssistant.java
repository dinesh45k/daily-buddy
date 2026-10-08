package com.lifeassistant.chat;

import com.lifeassistant.task.Task;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Simple rule-based assistant used when no AI API key is configured (offline mode). */
@Component
public class LocalAssistant {

    private static final String NOTE =
            "\n\n(Offline mode: no AI key is set, so this reply comes from built-in rules.)";

    public String reply(String message, List<Task> openTasks) {
        String m = message.toLowerCase(Locale.ROOT);

        if (containsAny(m, "plan", "today", "schedule", "day")) {
            return planDay(openTasks) + NOTE;
        }
        if (containsAny(m, "task", "todo", "to-do", "pending", "list")) {
            return openTasks.isEmpty()
                    ? "You have no open tasks. Add one on the left!" + NOTE
                    : "Your open tasks:\n" + format(openTasks) + NOTE;
        }
        if (containsAny(m, "habit", "routine")) {
            return "Habit tips: start tiny (2 minutes a day), attach it to something you already do, "
                    + "and never miss twice in a row." + NOTE;
        }
        if (containsAny(m, "study", "learn", "focus", "work")) {
            return "Focus tip: work in 25-minute blocks with 5-minute breaks, silence your phone, "
                    + "and write down the single goal of each block first." + NOTE;
        }
        if (containsAny(m, "stress", "tired", "sleep", "anxious")) {
            return "Take a short break: drink water, stretch, and take a few slow breaths. "
                    + "Aim for a regular sleep time, and pick just one small task to do next." + NOTE;
        }
        if (containsAny(m, "hello", "hi", "hey")) {
            return "Hi! I can plan your day, list your tasks, or give focus and habit tips. "
                    + "Try: \"plan my day\"." + NOTE;
        }
        return "I can help with: \"plan my day\", \"show my tasks\", \"habit tips\", or \"focus tips\"." + NOTE;
    }

    private String planDay(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "You have no open tasks, so your day is free. Add a task, or pick one small goal to start with.";
        }
        Task first = tasks.get(0);
        return "Here is a simple plan:\n"
                + "1. Start with: " + first.getTitle()
                + (first.getDueDate() != null ? " (due " + first.getDueDate() + ")" : "") + "\n"
                + "2. Then work through the rest in this order:\n" + format(tasks) + "\n"
                + "3. Use 25-minute focus blocks with short breaks, and review what is left in the evening.";
    }

    private String format(List<Task> tasks) {
        return tasks.stream()
                .map(t -> "- " + t.getTitle() + (t.getDueDate() != null ? " (due " + t.getDueDate() + ")" : ""))
                .collect(Collectors.joining("\n"));
    }

    private boolean containsAny(String text, String... words) {
        for (String w : words) {
            if (text.contains(w)) {
                return true;
            }
        }
        return false;
    }
}
