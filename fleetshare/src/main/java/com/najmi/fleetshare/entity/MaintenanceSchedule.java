package com.najmi.fleetshare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_schedules")
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "fleet_owner_id", nullable = false)
    private Long fleetOwnerId;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "maintenance_type", nullable = false, length = 100)
    private String maintenanceType;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type")
    private FrequencyType frequencyType;

    @Column(name = "frequency_value")
    private Integer frequencyValue;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "next_due_mileage")
    private Integer nextDueMileage;

    @Column(name = "last_performed_date")
    private LocalDate lastPerformedDate;

    @Column(name = "last_performed_mileage")
    private Integer lastPerformedMileage;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;

    public enum FrequencyType {
        DAILY, WEEKLY, MONTHLY, YEARLY, MILEAGE_BASED
    }

    public MaintenanceSchedule() {
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getFleetOwnerId() {
        return fleetOwnerId;
    }

    public void setFleetOwnerId(Long fleetOwnerId) {
        this.fleetOwnerId = fleetOwnerId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public Integer getFrequencyValue() {
        return frequencyValue;
    }

    public void setFrequencyValue(Integer frequencyValue) {
        this.frequencyValue = frequencyValue;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public Integer getNextDueMileage() {
        return nextDueMileage;
    }

    public void setNextDueMileage(Integer nextDueMileage) {
        this.nextDueMileage = nextDueMileage;
    }

    public LocalDate getLastPerformedDate() {
        return lastPerformedDate;
    }

    public void setLastPerformedDate(LocalDate lastPerformedDate) {
        this.lastPerformedDate = lastPerformedDate;
    }

    public Integer getLastPerformedMileage() {
        return lastPerformedMileage;
    }

    public void setLastPerformedMileage(Integer lastPerformedMileage) {
        this.lastPerformedMileage = lastPerformedMileage;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
