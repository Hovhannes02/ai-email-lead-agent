package com.hovhannes.leadagent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hovhannes.leadagent.model.LeadExtractionResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LeadExtractor {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public LeadExtractor(OpenAiClient openAiClient, ObjectMapper objectMapper) {
        this.openAiClient = openAiClient;
        this.objectMapper = objectMapper;
    }

    public LeadExtractionResult extract(String emailText) {
        Map<String, Object> itemSchema = Map.of(
            "type", "object",
            "properties", Map.of(
                "itemSku", nullableString(),
                "itemName", nullableString(),
                "quantity", nullableInteger(),
                "price", nullableNumber(),
                "currency", nullableString()
            ),
            "required", List.of(
                "itemSku",
                "itemName",
                "quantity",
                "price",
                "currency"
            ),
            "additionalProperties", false
        );

        Map<String, Object> schema = Map.of(
            "type", "object",
            "properties", Map.ofEntries(
                Map.entry("client", nullableString()),
                Map.entry("contactPerson", nullableString()),
                Map.entry("contactPhone", nullableString()),
                Map.entry("clientRequestNumber", nullableString()),
                Map.entry("subject", nullableString()),
                Map.entry("summary", nullableString()),
                Map.entry("urgency", nullableString()),
                Map.entry("priorityScore", nullableInteger()),
                Map.entry("tone", nullableString()),
                Map.entry("sentiment", nullableString()),
                Map.entry("language", nullableString()),
                Map.entry(
                    "items",
                    Map.of(
                        "type", "array",
                        "items", itemSchema
                    )
                )
            ),
            "required", List.of(
                "client",
                "contactPerson",
                "contactPhone",
                "clientRequestNumber",
                "subject",
                "summary",
                "urgency",
                "priorityScore",
                "tone",
                "sentiment",
                "language",
                "items"
            ),
            "additionalProperties", false
        );

        Map<String, Object> payload = Map.of(
            "model", openAiClient.getModel(),
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content",
                    """
                    Extract structured lead information from a business email.

                    Rules:
                    - Never invent company, contact, SKU, product, phone, or price data.
                    - If information is missing, return null.
                    - contactPerson may be the sender email when available in the input.
                    - Extract requested products into items.
                    - quantity must be an integer when explicitly known.
                    - If the customer requests a product but gives no quantity, use 1.
                    - price should be null when the email does not specify one.
                    - Detect urgency as low, medium, or high.
                    - priorityScore is an estimate from 1 to 10.
                    - tone is a short label such as serious, exploratory,
                      complaint, or negotiation.
                    - sentiment is a short label such as positive, neutral,
                      or negative.
                    - language is the detected email language.
                    - summary should be one concise sentence.
                    - clientRequestNumber must only be extracted if present.
                      Do not fabricate identifiers.
                    """
                ),
                Map.of("role", "user", "content", emailText)
            ),
            "response_format", Map.of(
                "type", "json_schema",
                "json_schema", Map.of(
                    "name", "lead_extraction",
                    "strict", true,
                    "schema", schema
                )
            )
        );

        JsonNode json = openAiClient.completeJson(payload);

        try {
            return objectMapper.treeToValue(json, LeadExtractionResult.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                "Could not map structured model output to LeadExtractionResult.",
                e
            );
        }
    }

    private Map<String, Object> nullableString() {
        return Map.of("type", List.of("string", "null"));
    }

    private Map<String, Object> nullableInteger() {
        return Map.of("type", List.of("integer", "null"));
    }

    private Map<String, Object> nullableNumber() {
        return Map.of("type", List.of("number", "null"));
    }
}
