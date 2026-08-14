package com.hovhannes.leadagent.service;

import com.hovhannes.leadagent.model.LeadExtractionResult;
import com.hovhannes.leadagent.model.LeadItem;
import com.hovhannes.leadagent.model.LeadValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeadValidatorTest {

    private final LeadValidator validator = new LeadValidator();

    @Test
    void completeLeadShouldBeValid() {
        LeadExtractionResult lead = new LeadExtractionResult();
        lead.setClient("Acme Trading");
        lead.setContactPerson("buyer@acme.example");

        LeadItem item = new LeadItem();
        item.setItemSku("SKU-44321");
        item.setQuantity(100);

        lead.setItems(List.of(item));

        LeadValidationResult result = validator.validate(lead);

        assertEquals(LeadValidationResult.Status.VALID, result.status());
        assertTrue(result.missingFields().isEmpty());
    }

    @Test
    void incompleteLeadShouldGoToHumanReview() {
        LeadExtractionResult lead = new LeadExtractionResult();
        lead.setContactPerson("buyer@example.com");
        lead.setItems(List.of());

        LeadValidationResult result = validator.validate(lead);

        assertEquals(
            LeadValidationResult.Status.NEEDS_HUMAN_REVIEW,
            result.status()
        );

        assertTrue(result.missingFields().contains("client"));
        assertTrue(result.missingFields().contains("items"));
    }

    @Test
    void missingQuantityShouldDefaultToOne() {
        LeadExtractionResult lead = new LeadExtractionResult();
        lead.setClient("Acme Trading");
        lead.setContactPerson("buyer@acme.example");

        LeadItem item = new LeadItem();
        item.setItemName("Industrial filter");
        item.setQuantity(null);

        lead.setItems(List.of(item));

        LeadValidationResult result = validator.validate(lead);

        assertEquals(LeadValidationResult.Status.VALID, result.status());
        assertEquals(1, item.getQuantity());
    }
}
