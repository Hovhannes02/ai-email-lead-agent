package com.hovhannes.leadagent.model;

public record LeadProcessingResult(
    boolean lead,
    String action,
    LeadExtractionResult extractedData,
    LeadValidationResult validation,
    String mockErpLeadId
) {
}
