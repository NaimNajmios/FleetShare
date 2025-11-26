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

    // Constructors
    public VehicleDTO() {
    }

    public VehicleDTO(Long vehicleId, String registrationNo, String model, String brand,
            Integer manufacturingYear, String category, BigDecimal ratePerDay,
            String vehicleImageUrl, String ownerBusinessName) {
        this.vehicleId = vehicleId;
        this.registrationNo = registrationNo;
        this.model = model;
        this.brand = brand;
        this.manufacturingYear = manufacturingYear;
        this.category = category;
        this.ratePerDay = ratePerDay;
        this.vehicleImageUrl = vehicleImageUrl;
        this.ownerBusinessName = ownerBusinessName;
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
}
