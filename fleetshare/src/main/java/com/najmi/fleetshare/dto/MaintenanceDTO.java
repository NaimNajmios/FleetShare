package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaintenanceDTO {
    private Long maintenanceId;
    private Long vehicleId;
    private String vehicleRegistrationNo;
    private String vehicleModel;
    private String vehicleBrand;
    private String description;
    private LocalDate scheduledDate;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private BigDecimal estimatedCost;
    private BigDecimal finalCost;
    private String status;
    private String ownerBusinessName;

    // Constructors
    public MaintenanceDTO() {
    }

    public MaintenanceDTO(Long maintenanceId, Long vehicleId, String vehicleRegistrationNo, String vehicleModel,
            String vehicleBrand, String description, LocalDate scheduledDate,
            LocalDateTime actualStartTime, LocalDateTime actualEndTime,
            BigDecimal estimatedCost, BigDecimal finalCost,
            String status, String ownerBusinessName) {
        this.maintenanceId = maintenanceId;
        this.vehicleId = vehicleId;
        this.vehicleRegistrationNo = vehicleRegistrationNo;
        this.vehicleModel = vehicleModel;
        this.vehicleBrand = vehicleBrand;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.estimatedCost = estimatedCost;
        this.finalCost = finalCost;
        this.status = status;
        this.ownerBusinessName = ownerBusinessName;
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

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerBusinessName() {
        return ownerBusinessName;
    }

    public void setOwnerBusinessName(String ownerBusinessName) {
        this.ownerBusinessName = ownerBusinessName;
    }
}
