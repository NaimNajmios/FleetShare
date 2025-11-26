package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MaintenanceDTO {
    private Long maintenanceId;
    private String vehicleRegistrationNo;
    private String vehicleModel;
    private String vehicleBrand;
    private String description;
    private LocalDate maintenanceDate;
    private BigDecimal cost;
    private String status;
    private String ownerBusinessName;

    // Constructors
    public MaintenanceDTO() {
    }

    public MaintenanceDTO(Long maintenanceId, String vehicleRegistrationNo, String vehicleModel,
            String vehicleBrand, String description, LocalDate maintenanceDate,
            BigDecimal cost, String status, String ownerBusinessName) {
        this.maintenanceId = maintenanceId;
        this.vehicleRegistrationNo = vehicleRegistrationNo;
        this.vehicleModel = vehicleModel;
        this.vehicleBrand = vehicleBrand;
        this.description = description;
        this.maintenanceDate = maintenanceDate;
        this.cost = cost;
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

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
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
