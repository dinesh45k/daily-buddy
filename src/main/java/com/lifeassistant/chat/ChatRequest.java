package com.lifeassistant.chat;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ChatRequest(@NotBlank String message, List<Turn> history) {
    public record Turn(String role, String content) {
    }
}
