package com.hovhannes.leadagent.service;

import com.hovhannes.leadagent.model.LeadExtractionResult;
import com.hovhannes.leadagent.model.LeadProcessingResult;
import com.hovhannes.leadagent.model.LeadValidationResult;
import org.springframework.stereotype.Service;

@Service
public class LeadProcessingService {

    private final LeadClassifier classifier;
    private final LeadExtractor extractor;
    private final LeadValidator validator;
    private final MockErpClient erpClient;

    public LeadProcessingService(
        LeadClassifier classifier,
        LeadExtractor extractor,
        LeadValidator validator,
        MockErpClient erpClient
    ) {
        this.classifier = classifier;
        this.extractor = extractor;
        this.validator = validator;
        this.erpClient = erpClient;
    }

    public LeadProcessingResult process(String emailText) {
        boolean isLead = classifier.isLead(emailText);

        if (!isLead) {
            return new LeadProcessingResult(
                false,
                "IGNORED_NON_LEAD",
                null,
                null,
                null
            );
        }

        LeadExtractionResult extracted = extractor.extract(emailText);
        LeadValidationResult validation = validator.validate(extracted);

        if (validation.status() != LeadValidationResult.Status.VALID) {
            return new LeadProcessingResult(
                true,
                "SENT_TO_HUMAN_REVIEW",
                extracted,
                validation,
                null
            );
        }

        String demoLeadId = erpClient.createLead(extracted);

        return new LeadProcessingResult(
            true,
            "CREATED_IN_MOCK_ERP",
            extracted,
            validation,
            demoLeadId
        );
    }
}
