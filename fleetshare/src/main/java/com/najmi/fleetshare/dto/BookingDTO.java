package com.najmi.fleetshare.dto;

import java.time.LocalDateTime;

public class BookingDTO {
    private Long bookingId;
    private String renterName;
    private String renterEmail;
    private String vehicleModel;
    private String vehicleBrand;
    private String vehicleRegistrationNo;
    private String ownerBusinessName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private LocalDateTime createdAt;

    // Constructors
    public BookingDTO() {
    }

    public BookingDTO(Long bookingId, String renterName, String renterEmail,
            String vehicleModel, String vehicleBrand, String vehicleRegistrationNo,
            String ownerBusinessName, LocalDateTime startDate, LocalDateTime endDate,
            String status, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.renterName = renterName;
        this.renterEmail = renterEmail;
        this.vehicleModel = vehicleModel;
        this.vehicleBrand = vehicleBrand;
        this.vehicleRegistrationNo = vehicleRegistrationNo;
        this.ownerBusinessName = ownerBusinessName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getRenterEmail() {
        return renterEmail;
    }

    public void setRenterEmail(String renterEmail) {
        this.renterEmail = renterEmail;
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

    public String getVehicleRegistrationNo() {
        return vehicleRegistrationNo;
    }

    public void setVehicleRegistrationNo(String vehicleRegistrationNo) {
        this.vehicleRegistrationNo = vehicleRegistrationNo;
    }

    public String getOwnerBusinessName() {
        return ownerBusinessName;
    }

    public void setOwnerBusinessName(String ownerBusinessName) {
        this.ownerBusinessName = ownerBusinessName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
