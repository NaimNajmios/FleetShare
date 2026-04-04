package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private Long serviceProviderId;
    private String serviceProviderName;
    private String maintenanceType;
    private String notes;
    private String attachments;
    private String serviceCenterName;
    private Boolean warrantyApplicable;
    private LocalDateTime createdAt;
    private List<PartDTO> parts;

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

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getServiceCenterName() {
        return serviceCenterName;
    }

    public void setServiceCenterName(String serviceCenterName) {
        this.serviceCenterName = serviceCenterName;
    }

    public Boolean getWarrantyApplicable() {
        return warrantyApplicable;
    }

    public void setWarrantyApplicable(Boolean warrantyApplicable) {
        this.warrantyApplicable = warrantyApplicable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<PartDTO> getParts() {
        return parts;
    }

    public void setParts(List<PartDTO> parts) {
        this.parts = parts;
    }
}
