package com.hovhannes.leadagent.service;

import com.hovhannes.leadagent.model.LeadExtractionResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockErpClient {

    /**
     * Portfolio-safe stand-in for the private company ERP integration.
     *
     * The original production implementation is intentionally not included.
     */
    public String createLead(LeadExtractionResult lead) {
        return "DEMO-" + UUID.randomUUID()
            .toString()
            .substring(0, 8)
            .toUpperCase();
    }
}
