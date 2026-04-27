package com.najmi.fleetshare.dto;

import java.util.List;
import java.util.Map;

/**
 * Generic response wrapper for report data
 * 
 * @param <T> The type of data rows in the report
 */
public class ReportResponse<T> {

    private String reportTitle;
    private String generatedAt;
    private String period;
    private String generatedBy;
    private String reportType;
    private String filters;
    private List<String> columns;
    private List<T> data;
    private Map<String, Object> summary;
    private ComparisonData comparisonData;
    private List<String> remarks;

    // Default constructor
    public ReportResponse() {
    }

    // Builder-style setters for fluent API
    public ReportResponse<T> withTitle(String title) {
        this.reportTitle = title;
        return this;
    }

    public ReportResponse<T> withGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
        return this;
    }

    public ReportResponse<T> withPeriod(String period) {
        this.period = period;
        return this;
    }

    public ReportResponse<T> withGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
        return this;
    }

    public ReportResponse<T> withReportType(String reportType) {
        this.reportType = reportType;
        return this;
    }

    public ReportResponse<T> withFilters(String filters) {
        this.filters = filters;
        return this;
    }

    public ReportResponse<T> withColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    public ReportResponse<T> withData(List<T> data) {
        this.data = data;
        return this;
    }

    public ReportResponse<T> withSummary(Map<String, Object> summary) {
        this.summary = summary;
        return this;
    }

    public ReportResponse<T> withComparisonData(ComparisonData comparisonData) {
        this.comparisonData = comparisonData;
        return this;
    }

    public ReportResponse<T> withRemarks(List<String> remarks) {
        this.remarks = remarks;
        return this;
    }

    // Standard Getters and Setters
    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    public ComparisonData getComparisonData() {
        return comparisonData;
    }

    public void setComparisonData(ComparisonData comparisonData) {
        this.comparisonData = comparisonData;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
    }
}
