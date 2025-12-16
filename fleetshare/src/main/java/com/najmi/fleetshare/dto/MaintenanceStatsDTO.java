package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * DTO for maintenance statistics used in dashboard charts
 */
public class MaintenanceStatsDTO {

    // Status counts
    private int pendingCount;
    private int inProgressCount;
    private int completedCount;
    private int cancelledCount;
    private int totalCount;

    // Cost aggregates
    private BigDecimal totalEstimatedCost;
    private BigDecimal totalFinalCost;
    private BigDecimal avgCostPerMaintenance;

    // Monthly data for charts (month name -> value)
    private Map<String, Integer> monthlyCountData;
    private Map<String, BigDecimal> monthlyCostData;

    // Constructors
    public MaintenanceStatsDTO() {
        this.pendingCount = 0;
        this.inProgressCount = 0;
        this.completedCount = 0;
        this.cancelledCount = 0;
        this.totalCount = 0;
        this.totalEstimatedCost = BigDecimal.ZERO;
        this.totalFinalCost = BigDecimal.ZERO;
        this.avgCostPerMaintenance = BigDecimal.ZERO;
        this.monthlyCountData = new LinkedHashMap<>();
        this.monthlyCostData = new LinkedHashMap<>();
    }

    // Getters and Setters
    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(int inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(int cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalEstimatedCost() {
        return totalEstimatedCost;
    }

    public void setTotalEstimatedCost(BigDecimal totalEstimatedCost) {
        this.totalEstimatedCost = totalEstimatedCost;
    }

    public BigDecimal getTotalFinalCost() {
        return totalFinalCost;
    }

    public void setTotalFinalCost(BigDecimal totalFinalCost) {
        this.totalFinalCost = totalFinalCost;
    }

    public BigDecimal getAvgCostPerMaintenance() {
        return avgCostPerMaintenance;
    }

    public void setAvgCostPerMaintenance(BigDecimal avgCostPerMaintenance) {
        this.avgCostPerMaintenance = avgCostPerMaintenance;
    }

    public Map<String, Integer> getMonthlyCountData() {
        return monthlyCountData;
    }

    public void setMonthlyCountData(Map<String, Integer> monthlyCountData) {
        this.monthlyCountData = monthlyCountData;
    }

    public Map<String, BigDecimal> getMonthlyCostData() {
        return monthlyCostData;
    }

    public void setMonthlyCostData(Map<String, BigDecimal> monthlyCostData) {
        this.monthlyCostData = monthlyCostData;
    }

    // Convenience method for completion rate
    public int getCompletionRate() {
        if (totalCount == 0)
            return 0;
        return (int) Math.round((completedCount * 100.0) / totalCount);
    }
}
