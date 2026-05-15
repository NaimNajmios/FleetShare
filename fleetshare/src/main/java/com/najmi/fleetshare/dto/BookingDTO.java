package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    // Vehicle details
    private String vehicleFuelType;
    private String vehicleTransmissionType;
    private String vehicleCategory;
    private BigDecimal vehicleRatePerDay;

    // Booking price snapshot fields
    private BigDecimal ratePerDay;
    private Integer daysRented;
    private String snapshotRemarks;
    private java.util.List<PriceAdjustment> adjustments = new java.util.ArrayList<>();

    // Additional contact details
    private String renterPhoneNumber;
    private String ownerContactPhone;
    private Boolean ownerIsVerified;

    // Invoice details
    private LocalDate invoiceIssueDate;
    private LocalDate invoiceDueDate;
    private String invoiceStatus;
    private String invoiceRemarks;

    // Payment details
    private LocalDateTime paymentDate;
    private String paymentTransactionReference;

    // Computed flags
    private Boolean startDatePassed;
    private Boolean startDateUpcoming;

    public static class PriceAdjustment {
        private String description;
        private BigDecimal amount;

        public PriceAdjustment() {}
        public PriceAdjustment(String description, BigDecimal amount) {
            this.description = description;
            this.amount = amount;
        }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    // Constructors
    public BookingDTO() {
    }

    public BookingDTO(Long bookingId, Long renterId, String renterName, String renterEmail,
            Long vehicleId, String vehicleModel, String vehicleBrand, String vehicleRegistrationNo,
            String vehicleImageUrl,
            String ownerBusinessName, LocalDateTime startDate, LocalDateTime endDate,
            String status, BigDecimal totalCost, String paymentMethod, String paymentStatus,
            String invoiceNumber, String proofOfPaymentUrl, LocalDateTime createdAt,
            String vehicleFuelType, String vehicleTransmissionType, String vehicleCategory,
            BigDecimal vehicleRatePerDay) {
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
        this.vehicleFuelType = vehicleFuelType;
        this.vehicleTransmissionType = vehicleTransmissionType;
        this.vehicleCategory = vehicleCategory;
        this.vehicleRatePerDay = vehicleRatePerDay;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    // Vehicle detail getters and setters
    public String getVehicleFuelType() {
        return vehicleFuelType;
    }

    public void setVehicleFuelType(String vehicleFuelType) {
        this.vehicleFuelType = vehicleFuelType;
    }

    public String getVehicleTransmissionType() {
        return vehicleTransmissionType;
    }

    public void setVehicleTransmissionType(String vehicleTransmissionType) {
        this.vehicleTransmissionType = vehicleTransmissionType;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public BigDecimal getVehicleRatePerDay() {
        return vehicleRatePerDay;
    }

    public void setVehicleRatePerDay(BigDecimal vehicleRatePerDay) {
        this.vehicleRatePerDay = vehicleRatePerDay;
    }

    public BigDecimal getRatePerDay() {
        return ratePerDay;
    }

    public void setRatePerDay(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public Integer getDaysRented() {
        return daysRented;
    }

    public void setDaysRented(Integer daysRented) {
        this.daysRented = daysRented;
    }

    public String getSnapshotRemarks() {
        return snapshotRemarks;
    }

    public void setSnapshotRemarks(String snapshotRemarks) {
        this.snapshotRemarks = snapshotRemarks;
    }

    public java.util.List<PriceAdjustment> getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(java.util.List<PriceAdjustment> adjustments) {
        this.adjustments = adjustments;
    }

    public String getRenterPhoneNumber() {
        return renterPhoneNumber;
    }

    public void setRenterPhoneNumber(String renterPhoneNumber) {
        this.renterPhoneNumber = renterPhoneNumber;
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

    public LocalDate getInvoiceIssueDate() {
        return invoiceIssueDate;
    }

    public void setInvoiceIssueDate(LocalDate invoiceIssueDate) {
        this.invoiceIssueDate = invoiceIssueDate;
    }

    public LocalDate getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(LocalDate invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceRemarks() {
        return invoiceRemarks;
    }

    public void setInvoiceRemarks(String invoiceRemarks) {
        this.invoiceRemarks = invoiceRemarks;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentTransactionReference() {
        return paymentTransactionReference;
    }

    public void setPaymentTransactionReference(String paymentTransactionReference) {
        this.paymentTransactionReference = paymentTransactionReference;
    }

    public Boolean getStartDatePassed() {
        return startDatePassed;
    }

    public void setStartDatePassed(Boolean startDatePassed) {
        this.startDatePassed = startDatePassed;
    }

    public Boolean getStartDateUpcoming() {
        return startDateUpcoming;
    }

    public void setStartDateUpcoming(Boolean startDateUpcoming) {
        this.startDateUpcoming = startDateUpcoming;
    }
}
