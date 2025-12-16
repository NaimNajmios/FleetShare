package com.najmi.fleetshare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiclemaintenance")
public class VehicleMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maintenance_id")
    private Long maintenanceId;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "fleet_owner_id", nullable = false)
    private Long fleetOwnerId;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "final_cost", precision = 10, scale = 2)
    private BigDecimal finalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private MaintenanceStatus currentStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Enum for status
    public enum MaintenanceStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

    // Constructors
    public VehicleMaintenance() {
    }

    // Getters and Setters
    public Long getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Long maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getFleetOwnerId() {
        return fleetOwnerId;
    }

    public void setFleetOwnerId(Long fleetOwnerId) {
        this.fleetOwnerId = fleetOwnerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(BigDecimal finalCost) {
        this.finalCost = finalCost;
    }

    public MaintenanceStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(MaintenanceStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
