package com.najmi.fleetshare.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for AI Data Assistant query responses.
 * Contains structured data that the frontend renders as a dynamic table.
 */
public class AiQueryResponse {

    private boolean success;
    private String explanation; // AI's natural-language explanation
    private List<String> columns; // Table column headers
    private List<Map<String, Object>> data; // Table rows
    private Map<String, Object> summary; // Summary metrics (e.g., totals)
    private String error; // Error message if failed

    public AiQueryResponse() {
    }

    // Static factory for error responses
    public static AiQueryResponse error(String errorMessage) {
        AiQueryResponse response = new AiQueryResponse();
        response.setSuccess(false);
        response.setError(errorMessage);
        return response;
    }

    // Static factory for success responses
    public static AiQueryResponse success(String explanation, List<String> columns,
            List<Map<String, Object>> data, Map<String, Object> summary) {
        AiQueryResponse response = new AiQueryResponse();
        response.setSuccess(true);
        response.setExplanation(explanation);
        response.setColumns(columns);
        response.setData(data);
        response.setSummary(summary);
        return response;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
