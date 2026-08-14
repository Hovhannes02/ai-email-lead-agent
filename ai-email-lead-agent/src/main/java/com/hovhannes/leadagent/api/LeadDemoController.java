package com.hovhannes.leadagent.api;

import com.hovhannes.leadagent.model.LeadProcessingResult;
import com.hovhannes.leadagent.service.LeadProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/demo/leads")
public class LeadDemoController {

    private final LeadProcessingService processingService;

    public LeadDemoController(LeadProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping("/process")
    public ResponseEntity<LeadProcessingResult> process(
        @RequestBody Map<String, String> body
    ) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(processingService.process(email));
    }
}
