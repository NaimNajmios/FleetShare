package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
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
    private Long vehicleId;
    private Long renterId;
    private BigDecimal totalCost;
    private String vehicleImageUrl;
    private String paymentMethod;
    private String paymentStatus;
    private String invoiceNumber;
    private String proofOfPaymentUrl;
    private LocalDateTime createdAt;

    // Constructors
    public BookingDTO() {
    }

    public BookingDTO(Long bookingId, Long renterId, String renterName, String renterEmail,
            Long vehicleId, String vehicleModel, String vehicleBrand, String vehicleRegistrationNo,
            String vehicleImageUrl,
            String ownerBusinessName, LocalDateTime startDate, LocalDateTime endDate,
            String status, BigDecimal totalCost, String paymentMethod, String paymentStatus,
            String invoiceNumber, String proofOfPaymentUrl, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.renterId = renterId;
        this.renterName = renterName;
        this.renterEmail = renterEmail;
        this.vehicleId = vehicleId;
        this.vehicleModel = vehicleModel;
        this.vehicleBrand = vehicleBrand;
        this.vehicleRegistrationNo = vehicleRegistrationNo;
        this.vehicleImageUrl = vehicleImageUrl;
        this.ownerBusinessName = ownerBusinessName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.totalCost = totalCost;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.invoiceNumber = invoiceNumber;
        this.proofOfPaymentUrl = proofOfPaymentUrl;
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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getVehicleImageUrl() {
        return vehicleImageUrl;
    }

    public void setVehicleImageUrl(String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getProofOfPaymentUrl() {
        return proofOfPaymentUrl;
    }

    public void setProofOfPaymentUrl(String proofOfPaymentUrl) {
        this.proofOfPaymentUrl = proofOfPaymentUrl;
    }
}
