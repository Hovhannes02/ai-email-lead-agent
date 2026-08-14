package com.hovhannes.leadagent.model;

import java.util.List;

public record LeadValidationResult(
    Status status,
    List<String> missingFields
) {
    public enum Status {
        VALID,
        NEEDS_HUMAN_REVIEW,
        INVALID
    }
}
