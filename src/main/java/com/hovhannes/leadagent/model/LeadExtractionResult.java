package com.hovhannes.leadagent.model;

import java.util.ArrayList;
import java.util.List;

public class LeadExtractionResult {

    private String client;
    private String contactPerson;
    private String contactPhone;
    private String clientRequestNumber;
    private String subject;
    private String summary;

    private String urgency;
    private Integer priorityScore;
    private String tone;
    private String sentiment;
    private String language;

    private List<LeadItem> items = new ArrayList<>();

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getClientRequestNumber() {
        return clientRequestNumber;
    }

    public void setClientRequestNumber(String clientRequestNumber) {
        this.clientRequestNumber = clientRequestNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public Integer getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Integer priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<LeadItem> getItems() {
        return items;
    }

    public void setItems(List<LeadItem> items) {
        this.items = items;
    }
}
