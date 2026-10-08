package com.lifeassistant.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.lifeassistant.config.AiProperties;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/** Thin client for the Anthropic Messages API. */
@Component
public class ClaudeClient {

    private final AiProperties props;
    private final RestClient http;

    public ClaudeClient(AiProperties props) {
        this.props = props;
        this.http = RestClient.builder().baseUrl(props.baseUrl()).build();
    }

    public String complete(String system, List<Map<String, String>> messages) {
        if (props.apiKey() == null || props.apiKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "ANTHROPIC_API_KEY is not set. Set it and restart the app.");
        }
        Map<String, Object> body = Map.of(
                "model", props.model(),
                "max_tokens", props.maxTokens(),
                "system", system,
                "messages", messages);
        try {
            JsonNode response = http.post()
                    .uri("/v1/messages")
                    .header("x-api-key", props.apiKey())
                    .header("anthropic-version", "2023-06-01")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            JsonNode text = response == null ? null : response.path("content").path(0).path("text");
            if (text == null || text.isMissingNode()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty response from AI provider");
            }
            return text.asText();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI provider call failed: " + e.getMessage());
        }
    }
}
