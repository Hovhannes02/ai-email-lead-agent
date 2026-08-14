package com.hovhannes.leadagent.service;

import com.hovhannes.leadagent.model.LeadExtractionResult;
import com.hovhannes.leadagent.model.LeadItem;
import com.hovhannes.leadagent.model.LeadValidationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeadValidator {

    public LeadValidationResult validate(LeadExtractionResult data) {
        if (data == null) {
            return new LeadValidationResult(
                LeadValidationResult.Status.INVALID,
                List.of("extractedData")
            );
        }

        List<String> missing = new ArrayList<>();

        if (isBlank(data.getClient())) {
            missing.add("client");
        }

        if (isBlank(data.getContactPerson())) {
            missing.add("contactPerson");
        }

        if (data.getItems() == null || data.getItems().isEmpty()) {
            missing.add("items");
        } else {
            normalizeItems(data.getItems());

            boolean hasUsableItem = data.getItems().stream()
                .anyMatch(this::hasProductIdentifier);

            if (!hasUsableItem) {
                missing.add("itemSkuOrName");
            }
        }

        if (missing.isEmpty()) {
            return new LeadValidationResult(
                LeadValidationResult.Status.VALID,
                List.of()
            );
        }

        return new LeadValidationResult(
            LeadValidationResult.Status.NEEDS_HUMAN_REVIEW,
            missing
        );
    }

    private void normalizeItems(List<LeadItem> items) {
        for (LeadItem item : items) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                item.setQuantity(1);
            }

            if (item.getPrice() != null && item.getPrice() < 0) {
                item.setPrice(null);
            }
        }
    }

    private boolean hasProductIdentifier(LeadItem item) {
        return !isBlank(item.getItemSku()) || !isBlank(item.getItemName());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
