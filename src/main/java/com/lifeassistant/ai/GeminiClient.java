package com.lifeassistant.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.lifeassistant.config.AiProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/** Thin client for the Google Gemini generateContent API. */
@Component
public class GeminiClient {

    private final AiProperties props;
    private final RestClient http;

    public GeminiClient(AiProperties props) {
        this.props = props;
        this.http = RestClient.builder().baseUrl("https://generativelanguage.googleapis.com").build();
    }

    public String complete(String system, List<Map<String, String>> turns) {
        List<Map<String, Object>> contents = new ArrayList<>();
        for (Map<String, String> t : turns) {
            String role = "assistant".equals(t.get("role")) ? "model" : "user";
            contents.add(Map.of("role", role, "parts", List.of(Map.of("text", t.get("content")))));
        }
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", system))),
                "contents", contents);
        try {
            JsonNode response = http.post()
                    .uri("/v1beta/models/{model}:generateContent", props.geminiModel())
                    .header("x-goog-api-key", props.geminiApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            JsonNode text = response == null ? null
                    : response.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (text == null || text.isMissingNode()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty response from Gemini");
            }
            return text.asText();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini call failed: " + e.getMessage());
        }
    }
}
