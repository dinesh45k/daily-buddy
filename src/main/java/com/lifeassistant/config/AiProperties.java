package com.lifeassistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public record AiProperties(String apiKey, String model, String baseUrl, int maxTokens,
                           String geminiApiKey, String geminiModel) {
}
