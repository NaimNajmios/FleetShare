package com.najmi.fleetshare.dto;

import java.math.BigDecimal;

public class VehicleDTO {
    private Long vehicleId;
    private String registrationNo;
    private String model;
    private String brand;
    private Integer manufacturingYear;
    private String category;
    private BigDecimal ratePerDay;
    private String vehicleImageUrl;
    private String ownerBusinessName;

    // New fields
    private String fuelType;
    private String transmissionType;
    private Integer mileage;
    private String status;
    private String ownerContactPhone;
    private Boolean ownerIsVerified;

    // Constructors
    public VehicleDTO() {
    }

    public VehicleDTO(Long vehicleId, String registrationNo, String model, String brand,
            Integer manufacturingYear, String category, BigDecimal ratePerDay,
            String vehicleImageUrl, String ownerBusinessName,
            String fuelType, String transmissionType, Integer mileage, String status,
            String ownerContactPhone, Boolean ownerIsVerified) {
        this.vehicleId = vehicleId;
        this.registrationNo = registrationNo;
        this.model = model;
        this.brand = brand;
        this.manufacturingYear = manufacturingYear;
        this.category = category;
        this.ratePerDay = ratePerDay;
        this.vehicleImageUrl = vehicleImageUrl;
        this.ownerBusinessName = ownerBusinessName;
        this.fuelType = fuelType;
        this.transmissionType = transmissionType;
        this.mileage = mileage;
        this.status = status;
        this.ownerContactPhone = ownerContactPhone;
        this.ownerIsVerified = ownerIsVerified;
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(Integer manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getRatePerDay() {
        return ratePerDay;
    }

    public void setRatePerDay(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public String getVehicleImageUrl() {
        return vehicleImageUrl;
    }

    public void setVehicleImageUrl(String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl;
    }

    public String getOwnerBusinessName() {
        return ownerBusinessName;
    }

    public void setOwnerBusinessName(String ownerBusinessName) {
        this.ownerBusinessName = ownerBusinessName;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerContactPhone() {
        return ownerContactPhone;
    }

    public void setOwnerContactPhone(String ownerContactPhone) {
        this.ownerContactPhone = ownerContactPhone;
    }

    public Boolean getOwnerIsVerified() {
        return ownerIsVerified;
    }

    public void setOwnerIsVerified(Boolean ownerIsVerified) {
        this.ownerIsVerified = ownerIsVerified;
    }
}
