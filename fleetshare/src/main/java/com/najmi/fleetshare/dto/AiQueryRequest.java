package com.najmi.fleetshare.dto;

/**
 * DTO for AI Data Assistant query requests
 */
public class AiQueryRequest {

    private String query; // Natural-language question from the user
    private String provider; // Optional: "groq", "cerebras", "openrouter"

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
}
