package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MaintenanceScheduleDTO {
    private Long scheduleId;
    private Long fleetOwnerId;
    private Long vehicleId;
    private String vehicleRegistrationNo;
    private String vehicleModel;
    private String maintenanceType;
    private String description;
    private String frequencyType;
    private Integer frequencyValue;
    private LocalDate nextDueDate;
    private Integer nextDueMileage;
    private LocalDate lastPerformedDate;
    private Integer lastPerformedMileage;
    private BigDecimal estimatedCost;
    private Boolean isActive;
    private String notes;

    public MaintenanceScheduleDTO() {
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

    public String getVehicleRegistrationNo() {
        return vehicleRegistrationNo;
    }

    public void setVehicleRegistrationNo(String vehicleRegistrationNo) {
        this.vehicleRegistrationNo = vehicleRegistrationNo;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
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

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
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
}
