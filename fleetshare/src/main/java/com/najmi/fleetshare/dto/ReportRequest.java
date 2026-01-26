package com.najmi.fleetshare.dto;

import java.time.LocalDate;

/**
 * DTO for report generation parameters
 */
public class ReportRequest {

    private String category; // booking, vehicle, payment, maintenance, user
    private String reportType; // monthly-revenue, utilization-rate, etc.
    private String duration; // today, last7, thisMonth, lastMonth, custom
    private LocalDate startDate; // for custom range
    private LocalDate endDate; // for custom range
    private String status; // filter by status
    private Long vehicleId; // filter by vehicle (Owner)
    private Long ownerId; // filter by owner (Admin)
    private Long requesterId; // the user requesting the report
    private boolean isAdmin; // context flag
    private String format; // pdf or csv

    // Default constructor
    public ReportRequest() {
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Calculate actual start date based on duration type
     */
    public LocalDate getEffectiveStartDate() {
        if ("custom".equals(duration) && startDate != null) {
            return startDate;
        }
        LocalDate today = LocalDate.now();
        return switch (duration) {
            case "today" -> today;
            case "last7" -> today.minusDays(7);
            case "thisMonth" -> today.withDayOfMonth(1);
            case "lastMonth" -> today.minusMonths(1).withDayOfMonth(1);
            default -> today.minusDays(30); // default to last 30 days
        };
    }

    /**
     * Calculate actual end date based on duration type
     */
    public LocalDate getEffectiveEndDate() {
        if ("custom".equals(duration) && endDate != null) {
            return endDate;
        }
        LocalDate today = LocalDate.now();
        return switch (duration) {
            case "today" -> today;
            case "last7" -> today;
            case "thisMonth" -> today;
            case "lastMonth" -> today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());
            default -> today;
        };
    }
}
