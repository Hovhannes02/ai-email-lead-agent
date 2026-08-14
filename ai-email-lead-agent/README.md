# AI Email Lead Agent

Portfolio recreation of an AI-powered email-to-ERP workflow I built for a real sales process.

> **Confidentiality note:** The original production implementation was integrated with a private company ERP and cannot be shared publicly. This repository recreates the core workflow with sanitized examples and a mock ERP client. It contains no proprietary source code, customer data, credentials, private API endpoints, or internal business logic.

## What it does

The demo processes an inbound business email through four steps:

```text
Inbound email
     |
     v
Lead classifier
     |
  +--+--+
  |     |
 NO     YES
  |      |
Ignore   v
       Structured extraction
              |
              v
          Validation
          /        \
         /          \
 Human review     Valid
                    |
                    v
               Mock ERP
```

### 1. Classify

`LeadClassifier` decides whether the email represents commercial buying intent.

Examples treated as leads:

- product inquiries
- quote requests
- pricing requests
- purchase/order requests

Examples rejected:

- support requests
- newsletters
- spam
- internal messages
- unrelated email

### 2. Extract structured lead data

`LeadExtractor` converts unstructured email text into structured fields including:

- client/company
- contact details
- requested products
- quantity
- price/currency when explicitly present
- urgency
- priority score
- tone
- sentiment
- detected language
- short summary

The model is instructed not to invent missing business data.

### 3. Validate before action

`LeadValidator` prevents incomplete AI output from automatically triggering a downstream business action.

Possible outcomes:

- `VALID`
- `NEEDS_HUMAN_REVIEW`
- `INVALID`

For example, a genuine lead with no identifiable company or requested item is routed to human review instead of being blindly created in the ERP.

### 4. Mock ERP action

`MockErpClient` simulates lead creation and returns a demo ID.

The real company ERP integration is intentionally excluded.

---

## Tech

- Java 17
- Spring Boot
- OpenAI API
- JSON Schema / Structured Outputs
- REST

## Project structure

```text
src/main/java/com/hovhannes/leadagent/
├── api/
│   └── LeadDemoController.java
├── model/
│   ├── LeadExtractionResult.java
│   ├── LeadItem.java
│   ├── LeadProcessingResult.java
│   └── LeadValidationResult.java
└── service/
    ├── OpenAiClient.java
    ├── LeadClassifier.java
    ├── LeadExtractor.java
    ├── LeadValidator.java
    ├── LeadProcessingService.java
    └── MockErpClient.java
```

## Run locally

### 1. Set your API key

macOS / Linux:

```bash
export OPENAI_API_KEY="your-key-here"
```

Optional model override:

```bash
export OPENAI_MODEL="gpt-4o-mini"
```

### 2. Start the app

```bash
mvn spring-boot:run
```

### 3. Send an example email

```bash
curl -X POST http://localhost:8080/api/demo/leads/process \
  -H "Content-Type: application/json" \
  -d '{
    "email": "From: buyer@acme.example\nSubject: Quote request\n\nHi, we are Acme Trading. Please quote 100 units of SKU-44321."
  }'
```

A successful lead can produce a result similar to:

```json
{
  "lead": true,
  "action": "CREATED_IN_MOCK_ERP",
  "extractedData": {
    "client": "Acme Trading",
    "contactPerson": "buyer@acme.example",
    "items": [
      {
        "itemSku": "SKU-44321",
        "quantity": 100
      }
    ]
  },
  "validation": {
    "status": "VALID",
    "missingFields": []
  },
  "mockErpLeadId": "DEMO-A1B2C3D4"
}
```

## Why I built this demo

The original workflow automated part of a real lead-management process:

1. detect new incoming email
2. determine whether the email is a sales lead
3. extract structured lead information with an LLM
4. validate the result
5. create qualified leads through ERP APIs

This public version focuses on the AI decision and validation layer while replacing private infrastructure with a safe mock implementation.

## Failure cases worth testing

A useful AI workflow is not just the happy path.

Try emails such as:

- a valid quote request with complete product details
- a valid lead with no quantity
- a valid lead with no company name
- a support request containing product names
- a supplier invoice
- an angry customer complaint
- an ambiguous "please contact me" message
- multilingual inquiries

The important behavior is not simply extracting data. It is deciding when the system has enough confidence and information to act automatically, and when a human should review the result.
