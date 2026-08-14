package com.hovhannes.leadagent.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LeadClassifier {

    private final OpenAiClient openAiClient;

    public LeadClassifier(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public boolean isLead(String emailText) {
        Map<String, Object> schema = Map.of(
            "type", "object",
            "properties", Map.of(
                "isLead", Map.of("type", "boolean"),
                "reason", Map.of("type", "string")
            ),
            "required", List.of("isLead", "reason"),
            "additionalProperties", false
        );

        Map<String, Object> payload = Map.of(
            "model", openAiClient.getModel(),
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content",
                    """
                    You classify inbound business emails.

                    A lead is an email that expresses commercial buying intent,
                    including:
                    - product inquiry
                    - quote request
                    - pricing request
                    - purchase/order request
                    - request to be contacted about buying

                    Not a lead:
                    - spam
                    - newsletters
                    - job applications
                    - internal messages
                    - technical support requests
                    - delivery complaints
                    - invoices from suppliers
                    - unrelated messages

                    Be conservative when intent is unclear.
                    """
                ),
                Map.of("role", "user", "content", emailText)
            ),
            "response_format", Map.of(
                "type", "json_schema",
                "json_schema", Map.of(
                    "name", "lead_classification",
                    "strict", true,
                    "schema", schema
                )
            )
        );

        JsonNode json = openAiClient.completeJson(payload);
        return json.path("isLead").asBoolean(false);
    }
}
