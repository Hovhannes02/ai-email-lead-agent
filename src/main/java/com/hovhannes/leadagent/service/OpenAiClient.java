package com.hovhannes.leadagent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class OpenAiClient {

    private static final String CHAT_COMPLETIONS_URL =
        "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public OpenAiClient(
        ObjectMapper objectMapper,
        @Value("${openai.api-key}") String apiKey,
        @Value("${openai.model:gpt-4o-mini}") String model
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String complete(Map<String, Object> payload) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "OPENAI_API_KEY is not configured. Set it as an environment variable."
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(payload, headers);

        try {
            JsonNode response = restTemplate.postForObject(
                CHAT_COMPLETIONS_URL,
                request,
                JsonNode.class
            );

            if (response == null) {
                throw new IllegalStateException("OpenAI returned an empty response.");
            }

            JsonNode contentNode = response
                .path("choices")
                .path(0)
                .path("message")
                .path("content");

            if (contentNode.isMissingNode() || contentNode.isNull()) {
                throw new IllegalStateException(
                    "OpenAI response did not contain message.content: " + response
                );
            }

            return contentNode.asText();

        } catch (RestClientException e) {
            throw new IllegalStateException("OpenAI request failed.", e);
        }
    }

    public JsonNode completeJson(Map<String, Object> payload) {
        String content = complete(payload);

        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                "Model returned content that could not be parsed as JSON.",
                e
            );
        }
    }

    public String getModel() {
        return model;
    }
}
