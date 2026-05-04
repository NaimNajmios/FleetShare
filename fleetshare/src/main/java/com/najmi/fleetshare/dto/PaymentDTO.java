package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {
    private Long paymentId;
    private String invoiceNumber;
    private String renterName;
    private String ownerBusinessName;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String transactionReference;
    private LocalDateTime bookingStartDate;
    private Long bookingId;
    private BigDecimal platformCommission;
    private BigDecimal ownerPayout;
    private Boolean splitPaymentEnabled;

    // Constructors
    public PaymentDTO() {
    }

    public PaymentDTO(Long paymentId, String invoiceNumber, String renterName,
            String ownerBusinessName, BigDecimal amount, String paymentMethod,
            String paymentStatus, LocalDateTime paymentDate, String transactionReference,
            LocalDateTime bookingStartDate, Long bookingId,
            BigDecimal platformCommission, BigDecimal ownerPayout, Boolean splitPaymentEnabled) {
        this.paymentId = paymentId;
        this.invoiceNumber = invoiceNumber;
        this.renterName = renterName;
        this.ownerBusinessName = ownerBusinessName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.transactionReference = transactionReference;
        this.bookingStartDate = bookingStartDate;
        this.bookingId = bookingId;
        this.platformCommission = platformCommission;
        this.ownerPayout = ownerPayout;
        this.splitPaymentEnabled = splitPaymentEnabled;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getOwnerBusinessName() {
        return ownerBusinessName;
    }

    public void setOwnerBusinessName(String ownerBusinessName) {
        this.ownerBusinessName = ownerBusinessName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(LocalDateTime bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getPlatformCommission() {
        return platformCommission;
    }

    public void setPlatformCommission(BigDecimal platformCommission) {
        this.platformCommission = platformCommission;
    }

    public BigDecimal getOwnerPayout() {
        return ownerPayout;
    }

    public void setOwnerPayout(BigDecimal ownerPayout) {
        this.ownerPayout = ownerPayout;
    }

    public Boolean getSplitPaymentEnabled() {
        return splitPaymentEnabled;
    }

    public void setSplitPaymentEnabled(Boolean splitPaymentEnabled) {
        this.splitPaymentEnabled = splitPaymentEnabled;
    }
}
