package com.najmi.fleetshare.dto;

import java.util.List;

/**
 * DTO for AI Data Assistant query requests
 */
public class AiQueryRequest {

    private String query; // Natural-language question from the user
    private String provider; // Optional: "groq", "cerebras", "openrouter"
    private List<String> remarks; // Custom remarks for the report

    public AiQueryRequest() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
    }
}
